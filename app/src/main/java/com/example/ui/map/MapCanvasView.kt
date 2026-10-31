package com.example.ui.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.ui.platform.LocalContext
import com.example.R
import com.example.model.MapOverlayMode
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import com.example.data.local.CustomPinEntity
import com.example.model.BossSpawn
import com.example.model.ExtractionPoint
import com.example.model.FloorLevel
import com.example.model.LockedDoorMarker
import com.example.model.LootCategory
import com.example.model.LootSpawn
import com.example.model.MapStructure
import com.example.model.QuestMarker
import com.example.model.Rarity
import com.example.model.TarkovMap
import com.example.model.VectorFeatureType
import com.example.ui.theme.RarityCommon
import com.example.ui.theme.RarityEpic
import com.example.ui.theme.RarityLegendary
import com.example.ui.theme.RarityRare
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.TacticalHazardRed
import com.example.ui.theme.TarkovBackground
import com.example.ui.theme.TarkovSurface
import com.example.ui.theme.TarkovSurfaceBorder
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

sealed class TacticalMarker {
    data class Loot(val spawn: LootSpawn) : TacticalMarker()
    data class Extract(val extract: ExtractionPoint) : TacticalMarker()
    data class Boss(val boss: BossSpawn) : TacticalMarker()
    data class Quest(val quest: QuestMarker) : TacticalMarker()
    data class LockedDoor(val lock: LockedDoorMarker) : TacticalMarker()
    data class UserPin(val pin: CustomPinEntity) : TacticalMarker()
}

@Composable
fun MapCanvasView(
    map: TarkovMap,
    is3DMode: Boolean,
    selectedFloorIndex: Int?, // null means ALL floors shown in 3D stacked mode
    rarityFilter: Set<Rarity>,
    categoryFilter: Set<LootCategory>,
    showExtracts: Boolean,
    showBosses: Boolean,
    showQuests: Boolean,
    showLockedDoors: Boolean = true,
    showContainers: Boolean = true,
    selectedContainerType: String? = null,
    showGrid: Boolean,
    showLabels: Boolean,
    overlayMode: MapOverlayMode = MapOverlayMode.SATELLITE,
    satelliteOpacity: Float = 0.92f,
    customPins: List<CustomPinEntity>,
    highlightMarkerId: String? = null,
    zoomScale: Float = 1.25f,
    panOffset: Offset = Offset.Zero,
    onZoomChange: (Float) -> Unit = {},
    onPanChange: (Offset) -> Unit = {},
    onMarkerSelected: (TacticalMarker) -> Unit,
    onMapLongPress: (Float, Float, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density
    val context = LocalContext.current

    // Preload satellite orthophoto bitmap if available for current map
    val mapDrawableRes = remember(map.id) {
        when (map.id.lowercase()) {
            "customs" -> R.drawable.img_customs_satellite
            "woods" -> R.drawable.img_woods_satellite
            "factory" -> R.drawable.img_factory_satellite
            "reserve" -> R.drawable.img_reserve_satellite
            "interchange" -> R.drawable.img_interchange_satellite
            "ground_zero" -> R.drawable.img_ground_zero_satellite
            "streets" -> R.drawable.img_streets_satellite
            "shoreline" -> R.drawable.img_shoreline_satellite
            "lighthouse" -> R.drawable.img_lighthouse_satellite
            "the_lab", "lab" -> R.drawable.img_lab_satellite
            else -> null
        }
    }

    val mapBitmap = remember(mapDrawableRes) {
        if (mapDrawableRes != null) {
            try {
                BitmapFactory.decodeResource(context.resources, mapDrawableRes)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    // Pulse animation for selected or high-value items
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )

    // Touch targets list for hit-testing
    var activeMarkerTouchList by remember { mutableStateOf<List<Pair<Offset, TacticalMarker>>>(emptyList()) }

    val currentZoom by rememberUpdatedState(zoomScale)
    val currentPan by rememberUpdatedState(panOffset)
    val currentMarkers by rememberUpdatedState(activeMarkerTouchList)
    val currentFloor by rememberUpdatedState(selectedFloorIndex)
    val currentIs3D by rememberUpdatedState(is3DMode)
    val currentOnZoomChange by rememberUpdatedState(onZoomChange)
    val currentOnPanChange by rememberUpdatedState(onPanChange)
    val currentOnMarkerSelected by rememberUpdatedState(onMarkerSelected)
    val currentOnMapLongPress by rememberUpdatedState(onMapLongPress)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F141C)) // Rich dark tactical slate
            .testTag("tarkov_map_canvas_container")
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val startTime = System.currentTimeMillis()
                    val startPos = down.position
                    var totalDrag = Offset.Zero
                    var isTapCandidate = true
                    var isLongPressTriggered = false

                    do {
                        val event = awaitPointerEvent()
                        val activeChanges = event.changes.filter { it.pressed }
                        val pointerCount = activeChanges.size

                        if (pointerCount >= 2) {
                            // Multi-touch pinch-to-zoom and two-finger pan
                            isTapCandidate = false
                            val zoomFactor = event.calculateZoom()
                            val panDelta = event.calculatePan()

                            if (zoomFactor != 1f || panDelta != Offset.Zero) {
                                val newZoom = (currentZoom * zoomFactor).coerceIn(0.5f, 5.0f)
                                currentOnZoomChange(newZoom)
                                currentOnPanChange(currentPan + panDelta)
                                event.changes.forEach { it.consume() }
                            }
                        } else if (pointerCount == 1) {
                            val change = activeChanges.first()
                            val dragDelta = change.position - change.previousPosition
                            totalDrag += dragDelta

                            if (totalDrag.getDistance() > (8f * density)) {
                                isTapCandidate = false
                                currentOnPanChange(currentPan + dragDelta)
                                change.consume()
                            } else if (isTapCandidate && !isLongPressTriggered && (System.currentTimeMillis() - startTime > 450)) {
                                isLongPressTriggered = true
                                isTapCandidate = false
                                val (normX, normY) = screenToMapCoordinates(
                                    startPos,
                                    size.width.toFloat(),
                                    size.height.toFloat(),
                                    currentZoom,
                                    currentPan,
                                    currentIs3D,
                                    currentFloor ?: 0
                                )
                                currentOnMapLongPress(normX, normY, currentFloor ?: 0)
                                change.consume()
                            }
                        }
                    } while (event.changes.any { it.pressed })

                    if (isTapCandidate && !isLongPressTriggered) {
                        val hit = currentMarkers.firstOrNull { (markerOffset, _) ->
                            val dx = markerOffset.x - startPos.x
                            val dy = markerOffset.y - startPos.y
                            sqrt((dx * dx + dy * dy).toDouble()) < (42.0 * density)
                        }
                        if (hit != null) {
                            currentOnMarkerSelected(hit.second)
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize().testTag("tarkov_map_canvas")) {
            val canvasW = size.width
            val canvasH = size.height

            // 1. Draw Military Perimeter Ground
            drawTacticalGround(canvasW, canvasH, zoomScale, panOffset, is3DMode, showGrid, density)

            // 2. Draw Satellite Orthophoto Reconnaissance Overlay
            if (mapBitmap != null && overlayMode != MapOverlayMode.TOPO_SCHEMATIC) {
                drawSatelliteOverlay(
                    bitmap = mapBitmap,
                    canvasW = canvasW,
                    canvasH = canvasH,
                    zoom = zoomScale,
                    pan = panOffset,
                    is3D = is3DMode,
                    selectedFloor = selectedFloorIndex,
                    overlayMode = overlayMode,
                    opacity = satelliteOpacity
                )
            }

            // 3. Draw Detailed Topographic Terrain & Zones (Contours, Roads, Water, Lab sectors)
            drawDetailedTopographicTerrain(
                map = map,
                canvasW = canvasW,
                canvasH = canvasH,
                zoom = zoomScale,
                pan = panOffset,
                is3D = is3DMode,
                selectedFloor = selectedFloorIndex,
                overlayMode = overlayMode,
                hasSatelliteBitmap = (mapBitmap != null),
                density = density
            )

            // 4. Draw Vector Topography Features (Roads, Water, Rail, Fences)
            map.vectorFeatures.forEach { feature ->
                drawVectorFeature(feature, canvasW, canvasH, zoomScale, panOffset, is3DMode, selectedFloorIndex, density, overlayMode)
            }

            // 5. Draw Structures, Floor Footprints & Room Partitions
            map.structures.forEach { structure ->
                drawStructure(structure, map.floors, canvasW, canvasH, zoomScale, panOffset, is3DMode, selectedFloorIndex, density, showLabels, overlayMode)
            }

            // 6. Draw Mil-Spec Border Coordinate Rulers & Scale
            if (showGrid) {
                drawTacticalBorderRulers(canvasW, canvasH, zoomScale, panOffset, is3DMode, density)
            }

            // 7. Project and Draw Interactive Markers
            val touchTargets = mutableListOf<Pair<Offset, TacticalMarker>>()

            // A. Extraction Markers (Drawn first so loot sits on top if overlapping)
            if (showExtracts) {
                map.extractions.forEach { extract ->
                    val isFloorActive = selectedFloorIndex == null || extract.floorIndex == selectedFloorIndex
                    val screenPos = mapToScreenCoordinates(
                        extract.x, extract.y, extract.floorIndex,
                        canvasW, canvasH, zoomScale, panOffset, is3DMode, selectedFloorIndex
                    )
                    val isHighlighted = highlightMarkerId == extract.id
                    drawExtractMarker(extract, screenPos, isFloorActive, showLabels, isHighlighted, pulseRadius, density)
                    touchTargets.add(screenPos to TacticalMarker.Extract(extract))
                }
            }

            // B. Scav Boss Markers
            if (showBosses) {
                map.bosses.forEach { boss ->
                    val isFloorActive = selectedFloorIndex == null || boss.floorIndex == selectedFloorIndex
                    val screenPos = mapToScreenCoordinates(
                        boss.x, boss.y, boss.floorIndex,
                        canvasW, canvasH, zoomScale, panOffset, is3DMode, selectedFloorIndex
                    )
                    val isHighlighted = highlightMarkerId == boss.name
                    drawBossMarker(boss, screenPos, isFloorActive, showLabels, isHighlighted, pulseRadius, density)
                    touchTargets.add(screenPos to TacticalMarker.Boss(boss))
                }
            }

            // C. Quests
            if (showQuests) {
                map.quests.forEach { quest ->
                    val isFloorActive = selectedFloorIndex == null || quest.floorIndex == selectedFloorIndex
                    val screenPos = mapToScreenCoordinates(
                        quest.x, quest.y, quest.floorIndex,
                        canvasW, canvasH, zoomScale, panOffset, is3DMode, selectedFloorIndex
                    )
                    drawQuestMarker(quest, screenPos, isFloorActive, showLabels, density)
                    touchTargets.add(screenPos to TacticalMarker.Quest(quest))
                }
            }

            // D. Locked Doors & Keys (Tarkov.dev Parity)
            if (showLockedDoors) {
                map.lockedDoors.forEach { lock ->
                    val isFloorActive = selectedFloorIndex == null || lock.floorIndex == selectedFloorIndex
                    val screenPos = mapToScreenCoordinates(
                        lock.x, lock.y, lock.floorIndex,
                        canvasW, canvasH, zoomScale, panOffset, is3DMode, selectedFloorIndex
                    )
                    val isHighlighted = highlightMarkerId == lock.id
                    drawLockedDoorMarker(lock, screenPos, isFloorActive, showLabels, isHighlighted, pulseRadius, density)
                    touchTargets.add(screenPos to TacticalMarker.LockedDoor(lock))
                }
            }

            // E. Loot & Container Markers
            map.lootSpawns.forEach { loot ->
                val isContainer = loot.category == LootCategory.CONTAINER ||
                        loot.containerType in listOf("Safe", "PC Block", "Medcase", "Weapon Box", "Jacket", "Toolbox", "Dead Scav", "Duffle Bag", "Grenade Box")
                val containerPasses = if (isContainer) {
                    showContainers && (selectedContainerType == null || loot.containerType.contains(selectedContainerType, ignoreCase = true))
                } else true

                if (containerPasses && rarityFilter.contains(loot.rarity) && categoryFilter.contains(loot.category)) {
                    val isFloorActive = selectedFloorIndex == null || loot.floorIndex == selectedFloorIndex
                    val screenPos = mapToScreenCoordinates(
                        loot.x, loot.y, loot.floorIndex,
                        canvasW, canvasH, zoomScale, panOffset, is3DMode, selectedFloorIndex
                    )
                    val isHighlighted = highlightMarkerId == loot.id
                    drawLootMarker(loot, screenPos, isFloorActive, showLabels, isHighlighted, pulseRadius, density)
                    touchTargets.add(screenPos to TacticalMarker.Loot(loot))
                }
            }

            // F. Custom User Pins
            customPins.forEach { pin ->
                val isFloorActive = selectedFloorIndex == null || pin.floorIndex == selectedFloorIndex
                val screenPos = mapToScreenCoordinates(
                    pin.x, pin.y, pin.floorIndex,
                    canvasW, canvasH, zoomScale, panOffset, is3DMode, selectedFloorIndex
                )
                drawUserPinMarker(pin, screenPos, isFloorActive, showLabels, density)
                touchTargets.add(screenPos to TacticalMarker.UserPin(pin))
            }

            activeMarkerTouchList = touchTargets
        }
    }
}

private fun mapToScreenCoordinates(
    mapX: Float,
    mapY: Float,
    floorIndex: Int,
    canvasW: Float,
    canvasH: Float,
    zoom: Float,
    pan: Offset,
    is3D: Boolean,
    selectedFloor: Int?
): Offset {
    val centerX = canvasW / 2f
    val centerY = canvasH / 2f

    val normX = (mapX - 500f)
    val normY = (mapY - 500f)

    if (!is3D) {
        // Flat 2D Top-Down Projection
        return Offset(
            x = centerX + normX * zoom + pan.x,
            y = centerY + normY * zoom + pan.y
        )
    } else {
        // High-legibility 3D Axonometric projection
        // Gentle 25-degree isometric angle for superior screen visibility
        val rad = Math.toRadians(25.0)
        val cosA = cos(rad).toFloat()
        val sinA = sin(rad).toFloat()

        val isoX = (normX - normY) * cosA
        val isoY = (normX + normY) * sinA * 0.72f

        // Stacking floor elevation separation
        val elevationStep = if (selectedFloor == null) 42f else 16f
        val elevationOffset = floorIndex * elevationStep * zoom

        return Offset(
            x = centerX + isoX * zoom + pan.x,
            y = centerY + (isoY * zoom) - elevationOffset + pan.y
        )
    }
}

private fun screenToMapCoordinates(
    screen: Offset,
    canvasW: Float,
    canvasH: Float,
    zoom: Float,
    pan: Offset,
    is3D: Boolean,
    floorIndex: Int
): Pair<Float, Float> {
    val centerX = canvasW / 2f
    val centerY = canvasH / 2f

    if (!is3D) {
        val normX = (screen.x - centerX - pan.x) / zoom
        val normY = (screen.y - centerY - pan.y) / zoom
        return Pair((normX + 500f).coerceIn(0f, 1000f), (normY + 500f).coerceIn(0f, 1000f))
    } else {
        val elevationStep = 16f
        val elevationOffset = floorIndex * elevationStep * zoom
        val adjY = (screen.y - centerY - pan.y + elevationOffset) / zoom
        val adjX = (screen.x - centerX - pan.x) / zoom

        val rad = Math.toRadians(25.0)
        val cosA = cos(rad).toFloat()
        val sinA = sin(rad).toFloat() * 0.72f

        val normX = (adjX / (2 * cosA)) + (adjY / (2 * sinA))
        val normY = (adjY / (2 * sinA)) - (adjX / (2 * cosA))

        return Pair((normX + 500f).coerceIn(0f, 1000f), (normY + 500f).coerceIn(0f, 1000f))
    }
}

private fun DrawScope.drawTacticalGround(
    canvasW: Float,
    canvasH: Float,
    zoom: Float,
    pan: Offset,
    is3D: Boolean,
    showGrid: Boolean,
    density: Float
) {
    // 1. Draw Map Perimeter Ground Poly (1000x1000 area)
    val pTopLeft = mapToScreenCoordinates(0f, 0f, 0, canvasW, canvasH, zoom, pan, is3D, 0)
    val pTopRight = mapToScreenCoordinates(1000f, 0f, 0, canvasW, canvasH, zoom, pan, is3D, 0)
    val pBottomRight = mapToScreenCoordinates(1000f, 1000f, 0, canvasW, canvasH, zoom, pan, is3D, 0)
    val pBottomLeft = mapToScreenCoordinates(0f, 1000f, 0, canvasW, canvasH, zoom, pan, is3D, 0)

    val groundPath = Path().apply {
        moveTo(pTopLeft.x, pTopLeft.y)
        lineTo(pTopRight.x, pTopRight.y)
        lineTo(pBottomRight.x, pBottomRight.y)
        lineTo(pBottomLeft.x, pBottomLeft.y)
        close()
    }

    // High-contrast outdoor ground surface
    drawPath(
        path = groundPath,
        color = Color(0xFF111722), // deep military tactical ground
        style = Fill
    )

    // Outer boundary border
    drawPath(
        path = groundPath,
        color = Color(0xFF2E3E52),
        style = Stroke(width = 2.5f * density)
    )

    // 2. Tactical coordinate grid (A1, A2... E5)
    if (showGrid) {
        val gridColor = Color(0xFF222F3E).copy(alpha = 0.70f)

        for (gx in 100..900 step 150) {
            val p1 = mapToScreenCoordinates(gx.toFloat(), 0f, 0, canvasW, canvasH, zoom, pan, is3D, 0)
            val p2 = mapToScreenCoordinates(gx.toFloat(), 1000f, 0, canvasW, canvasH, zoom, pan, is3D, 0)
            drawLine(
                color = gridColor,
                start = p1,
                end = p2,
                strokeWidth = 1f * density,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            )
        }

        for (gy in 100..900 step 150) {
            val p1 = mapToScreenCoordinates(0f, gy.toFloat(), 0, canvasW, canvasH, zoom, pan, is3D, 0)
            val p2 = mapToScreenCoordinates(1000f, gy.toFloat(), 0, canvasW, canvasH, zoom, pan, is3D, 0)
            drawLine(
                color = gridColor,
                start = p1,
                end = p2,
                strokeWidth = 1f * density,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            )
        }
    }
}

private fun DrawScope.drawSatelliteOverlay(
    bitmap: Bitmap,
    canvasW: Float,
    canvasH: Float,
    zoom: Float,
    pan: Offset,
    is3D: Boolean,
    selectedFloor: Int?,
    overlayMode: MapOverlayMode,
    opacity: Float
) {
    val alphaFactor = if (overlayMode == MapOverlayMode.TACTICAL_HYBRID) 0.65f else 1.0f
    val effectiveAlpha = (opacity * alphaFactor).coerceIn(0.1f, 1.0f)
    val alphaInt = (effectiveAlpha * 255).toInt().coerceIn(0, 255)

    val paint = Paint().apply {
        isFilterBitmap = true
        isDither = true
        alpha = alphaInt
    }

    if (!is3D) {
        val pTopLeft = mapToScreenCoordinates(0f, 0f, 0, canvasW, canvasH, zoom, pan, false, selectedFloor)
        val pBottomRight = mapToScreenCoordinates(1000f, 1000f, 0, canvasW, canvasH, zoom, pan, false, selectedFloor)

        val dstRect = Rect(
            pTopLeft.x.toInt(),
            pTopLeft.y.toInt(),
            pBottomRight.x.toInt(),
            pBottomRight.y.toInt()
        )
        drawContext.canvas.nativeCanvas.drawBitmap(bitmap, null, dstRect, paint)
    } else {
        val pTL = mapToScreenCoordinates(0f, 0f, 0, canvasW, canvasH, zoom, pan, true, selectedFloor)
        val pTR = mapToScreenCoordinates(1000f, 0f, 0, canvasW, canvasH, zoom, pan, true, selectedFloor)
        val pBR = mapToScreenCoordinates(1000f, 1000f, 0, canvasW, canvasH, zoom, pan, true, selectedFloor)
        val pBL = mapToScreenCoordinates(0f, 1000f, 0, canvasW, canvasH, zoom, pan, true, selectedFloor)

        val src = floatArrayOf(
            0f, 0f,
            bitmap.width.toFloat(), 0f,
            bitmap.width.toFloat(), bitmap.height.toFloat(),
            0f, bitmap.height.toFloat()
        )
        val dst = floatArrayOf(
            pTL.x, pTL.y,
            pTR.x, pTR.y,
            pBR.x, pBR.y,
            pBL.x, pBL.y
        )
        val matrix = Matrix()
        if (matrix.setPolyToPoly(src, 0, dst, 0, 4)) {
            val nativeCanvas = drawContext.canvas.nativeCanvas
            nativeCanvas.save()
            nativeCanvas.concat(matrix)
            nativeCanvas.drawBitmap(bitmap, 0f, 0f, paint)
            nativeCanvas.restore()
        }
    }
}

private fun DrawScope.drawDetailedTopographicTerrain(
    map: TarkovMap,
    canvasW: Float,
    canvasH: Float,
    zoom: Float,
    pan: Offset,
    is3D: Boolean,
    selectedFloor: Int?,
    overlayMode: MapOverlayMode,
    hasSatelliteBitmap: Boolean,
    density: Float
) {
    val contourAlpha = when {
        overlayMode == MapOverlayMode.TOPO_SCHEMATIC -> 0.45f
        !hasSatelliteBitmap -> 0.35f
        else -> 0.18f
    }
    val contourColor = Color(0xFF38BDF8).copy(alpha = contourAlpha)

    // 1. Topographic Elevation Contour Lines
    val contourLevels = listOf(
        Pair("30m", listOf(50f to 200f, 250f to 180f, 450f to 240f, 700f to 210f, 950f to 260f)),
        Pair("50m", listOf(50f to 380f, 280f to 340f, 520f to 390f, 750f to 360f, 950f to 420f)),
        Pair("75m", listOf(60f to 580f, 300f to 550f, 550f to 600f, 780f to 560f, 940f to 620f)),
        Pair("95m", listOf(70f to 760f, 290f to 720f, 540f to 770f, 760f to 740f, 930f to 800f))
    )

    contourLevels.forEach { (elevLabel, points) ->
        val cPath = Path()
        val firstPt = mapToScreenCoordinates(points[0].first, points[0].second, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
        cPath.moveTo(firstPt.x, firstPt.y)

        for (i in 1 until points.size) {
            val pt = mapToScreenCoordinates(points[i].first, points[i].second, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
            cPath.lineTo(pt.x, pt.y)
        }

        drawPath(
            path = cPath,
            color = contourColor,
            style = Stroke(
                width = 1.2f * density,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 6f), 0f)
            )
        )

        // Elevation tag
        if (overlayMode == MapOverlayMode.TOPO_SCHEMATIC || !hasSatelliteBitmap) {
            val labelPos = mapToScreenCoordinates(points[2].first, points[2].second, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
            val paint = Paint().apply {
                color = android.graphics.Color.argb((contourAlpha * 255).toInt(), 56, 189, 248)
                textSize = 8.5f * density
                typeface = Typeface.MONOSPACE
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText(elevLabel, labelPos.x, labelPos.y - 3f, paint)
        }
    }

    // 2. Map-Specific Specialized Base Overlays
    when (map.id.lowercase()) {
        "streets" -> {
            // Avenues and boulevards
            drawTacticalBoulevard(100f, 520f, 900f, 520f, 40f, canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
            drawTacticalBoulevard(460f, 100f, 460f, 900f, 36f, canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
            drawTacticalBoulevard(680f, 120f, 680f, 880f, 30f, canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
            // Pedestrian crosswalks at main intersections
            drawCrosswalk(460f, 520f, canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
            drawCrosswalk(680f, 520f, canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
        }
        "the_lab" -> {
            // Underground classified research facility sectors
            // Violet Sector
            drawLabSector(200f, 200f, 400f, 380f, Color(0xFF8B5CF6), "VIOLET LAB // R&D", canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
            // Blue Sector
            drawLabSector(600f, 200f, 800f, 380f, Color(0xFF38BDF8), "BLUE LAB // QUARANTINE", canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
            // Green Sector
            drawLabSector(200f, 620f, 400f, 800f, Color(0xFF10B981), "GREEN LAB // HYDROPONICS", canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
            // Yellow Sector
            drawLabSector(600f, 620f, 800f, 800f, Color(0xFFFBBF24), "YELLOW LAB // CATWALKS", canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
            // Central Glass Atrium
            drawLabSector(420f, 420f, 580f, 580f, Color(0xFF00E5FF), "TERRAGROUP ATRIUM", canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
        }
        "shoreline" -> {
            // Coastal sea water zone at south
            drawWaterZone(0f, 800f, 1000f, 1000f, canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
            // Beach sand buffer
            drawTerrainBand(0f, 760f, 1000f, 800f, Color(0xFF332D22).copy(alpha = 0.5f), canvasW, canvasH, zoom, pan, is3D, selectedFloor)
            // Sanatorium helipad
            drawHelipad(500f, 400f, canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
        }
        "lighthouse" -> {
            // Western coastal sea bay
            drawWaterZone(0f, 200f, 240f, 1000f, canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
            // Mountain sniper cliffs
            drawTerrainBand(600f, 120f, 960f, 680f, Color(0xFF263238).copy(alpha = 0.45f), canvasW, canvasH, zoom, pan, is3D, selectedFloor)
        }
        "ground_zero" -> {
            // Main Primorsky Avenue and cross Mira Avenue
            drawTacticalBoulevard(100f, 520f, 900f, 520f, 38f, canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
            drawTacticalBoulevard(500f, 150f, 500f, 850f, 32f, canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
            // Pedestrian crosswalks at central intersection
            drawCrosswalk(500f, 520f, canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
            // TerraGroup Corporate Plaza courtyard
            drawPlazaCourtyard(390f, 340f, 590f, 510f, canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
            // Sniper warning boundary zone (Mira Ave extract kill-zone warning)
            drawSniperDangerZone(680f, 340f, 860f, 420f, "MIRA AVE // SNIPER ZONE (GREEN FLARE REQ)", canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
            // Metropolitan street trench barricades
            drawBarricadeLine(360f, 490f, 640f, 490f, canvasW, canvasH, zoom, pan, is3D, selectedFloor, density)
        }
    }
}

private fun DrawScope.drawTacticalBoulevard(
    x1: Float, y1: Float, x2: Float, y2: Float, widthUnits: Float,
    canvasW: Float, canvasH: Float, zoom: Float, pan: Offset, is3D: Boolean, selectedFloor: Int?, density: Float
) {
    val p1 = mapToScreenCoordinates(x1, y1, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val p2 = mapToScreenCoordinates(x2, y2, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val strokeW = (widthUnits * zoom * density * 0.35f).coerceIn(12f, 50f)

    // Dark asphalt road casing
    drawLine(Color(0xFF1E2530).copy(alpha = 0.85f), p1, p2, strokeW)
    // Road pavement
    drawLine(Color(0xFF2D3748).copy(alpha = 0.75f), p1, p2, strokeW * 0.82f)
    // Center divider dash
    drawLine(
        Color(0xFFFBBF24).copy(alpha = 0.65f),
        p1, p2,
        strokeWidth = 1.5f * density,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
    )
}

private fun DrawScope.drawCrosswalk(
    x: Float, y: Float,
    canvasW: Float, canvasH: Float, zoom: Float, pan: Offset, is3D: Boolean, selectedFloor: Int?, density: Float
) {
    val center = mapToScreenCoordinates(x, y, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val paint = Paint().apply {
        color = android.graphics.Color.argb(120, 241, 245, 249)
        strokeWidth = 2f * density
        style = Paint.Style.STROKE
    }
    for (i in -3..3) {
        val offsetPx = i * 4f * density
        drawContext.canvas.nativeCanvas.drawLine(
            center.x - 12f * density, center.y + offsetPx,
            center.x + 12f * density, center.y + offsetPx,
            paint
        )
    }
}

private fun DrawScope.drawLabSector(
    left: Float, top: Float, right: Float, bottom: Float,
    accentColor: Color, label: String,
    canvasW: Float, canvasH: Float, zoom: Float, pan: Offset, is3D: Boolean, selectedFloor: Int?, density: Float
) {
    val pTL = mapToScreenCoordinates(left, top, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val pTR = mapToScreenCoordinates(right, top, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val pBR = mapToScreenCoordinates(right, bottom, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val pBL = mapToScreenCoordinates(left, bottom, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)

    val path = Path().apply {
        moveTo(pTL.x, pTL.y)
        lineTo(pTR.x, pTR.y)
        lineTo(pBR.x, pBR.y)
        lineTo(pBL.x, pBL.y)
        close()
    }

    // Sector tinted fill
    drawPath(path = path, color = accentColor.copy(alpha = 0.16f), style = Fill)
    // Sector perimeter border
    drawPath(path = path, color = accentColor.copy(alpha = 0.65f), style = Stroke(width = 1.8f * density))

    // Sector designation label
    val centerPos = mapToScreenCoordinates((left + right) / 2f, (top + bottom) / 2f, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val paint = Paint().apply {
        color = accentColor.toArgb()
        textSize = 9f * density
        typeface = Typeface.MONOSPACE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    drawContext.canvas.nativeCanvas.drawText(label, centerPos.x, centerPos.y, paint)
}

private fun DrawScope.drawWaterZone(
    left: Float, top: Float, right: Float, bottom: Float,
    canvasW: Float, canvasH: Float, zoom: Float, pan: Offset, is3D: Boolean, selectedFloor: Int?, density: Float
) {
    val pTL = mapToScreenCoordinates(left, top, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val pTR = mapToScreenCoordinates(right, top, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val pBR = mapToScreenCoordinates(right, bottom, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val pBL = mapToScreenCoordinates(left, bottom, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)

    val path = Path().apply {
        moveTo(pTL.x, pTL.y)
        lineTo(pTR.x, pTR.y)
        lineTo(pBR.x, pBR.y)
        lineTo(pBL.x, pBL.y)
        close()
    }
    drawPath(path = path, color = Color(0xFF0F2642).copy(alpha = 0.85f), style = Fill)
    drawPath(path = path, color = Color(0xFF38BDF8).copy(alpha = 0.50f), style = Stroke(width = 2f * density))
}

private fun DrawScope.drawTerrainBand(
    left: Float, top: Float, right: Float, bottom: Float,
    color: Color,
    canvasW: Float, canvasH: Float, zoom: Float, pan: Offset, is3D: Boolean, selectedFloor: Int?
) {
    val pTL = mapToScreenCoordinates(left, top, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val pTR = mapToScreenCoordinates(right, top, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val pBR = mapToScreenCoordinates(right, bottom, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val pBL = mapToScreenCoordinates(left, bottom, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)

    val path = Path().apply {
        moveTo(pTL.x, pTL.y)
        lineTo(pTR.x, pTR.y)
        lineTo(pBR.x, pBR.y)
        lineTo(pBL.x, pBL.y)
        close()
    }
    drawPath(path = path, color = color, style = Fill)
}

private fun DrawScope.drawHelipad(
    x: Float, y: Float,
    canvasW: Float, canvasH: Float, zoom: Float, pan: Offset, is3D: Boolean, selectedFloor: Int?, density: Float
) {
    val center = mapToScreenCoordinates(x, y, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val radius = 18f * zoom * density
    drawCircle(Color(0xFF334155), radius = radius.coerceIn(8f, 32f), center = center, style = Fill)
    drawCircle(TacticalAmber, radius = radius.coerceIn(8f, 32f), center = center, style = Stroke(width = 1.5f * density))

    val paint = Paint().apply {
        color = TacticalAmber.toArgb()
        textSize = 14f * density
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    drawContext.canvas.nativeCanvas.drawText("H", center.x, center.y + (5f * density), paint)
}

private fun DrawScope.drawBarricadeLine(
    x1: Float, y1: Float, x2: Float, y2: Float,
    canvasW: Float, canvasH: Float, zoom: Float, pan: Offset, is3D: Boolean, selectedFloor: Int?, density: Float
) {
    val p1 = mapToScreenCoordinates(x1, y1, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val p2 = mapToScreenCoordinates(x2, y2, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    drawLine(
        color = TacticalHazardRed,
        start = p1,
        end = p2,
        strokeWidth = 3f * density,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
    )
}

private fun DrawScope.drawPlazaCourtyard(
    left: Float, top: Float, right: Float, bottom: Float,
    canvasW: Float, canvasH: Float, zoom: Float, pan: Offset, is3D: Boolean, selectedFloor: Int?, density: Float
) {
    val pTL = mapToScreenCoordinates(left, top, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val pTR = mapToScreenCoordinates(right, top, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val pBR = mapToScreenCoordinates(right, bottom, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val pBL = mapToScreenCoordinates(left, bottom, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)

    val path = Path().apply {
        moveTo(pTL.x, pTL.y)
        lineTo(pTR.x, pTR.y)
        lineTo(pBR.x, pBR.y)
        lineTo(pBL.x, pBL.y)
        close()
    }
    // Corporate granite pavers tint
    drawPath(path = path, color = Color(0xFF1E293B).copy(alpha = 0.60f), style = Fill)
    drawPath(path = path, color = Color(0xFF64748B).copy(alpha = 0.50f), style = Stroke(width = 1.5f * density))

    val centerPos = mapToScreenCoordinates((left + right) / 2f, (top + bottom) / 2f, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val paint = Paint().apply {
        color = android.graphics.Color.argb(140, 148, 163, 184)
        textSize = 8.5f * density
        typeface = Typeface.MONOSPACE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    drawContext.canvas.nativeCanvas.drawText("TERRAGROUP PLAZA", centerPos.x, centerPos.y, paint)
}

private fun DrawScope.drawSniperDangerZone(
    left: Float, top: Float, right: Float, bottom: Float,
    warningText: String,
    canvasW: Float, canvasH: Float, zoom: Float, pan: Offset, is3D: Boolean, selectedFloor: Int?, density: Float
) {
    val pTL = mapToScreenCoordinates(left, top, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val pTR = mapToScreenCoordinates(right, top, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val pBR = mapToScreenCoordinates(right, bottom, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val pBL = mapToScreenCoordinates(left, bottom, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)

    val path = Path().apply {
        moveTo(pTL.x, pTL.y)
        lineTo(pTR.x, pTR.y)
        lineTo(pBR.x, pBR.y)
        lineTo(pBL.x, pBL.y)
        close()
    }
    // Hazard diagonal red fill
    drawPath(path = path, color = TacticalHazardRed.copy(alpha = 0.15f), style = Fill)
    drawPath(
        path = path,
        color = TacticalHazardRed.copy(alpha = 0.75f),
        style = Stroke(
            width = 2f * density,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
        )
    )

    val centerPos = mapToScreenCoordinates((left + right) / 2f, (top + bottom) / 2f, 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
    val paint = Paint().apply {
        color = android.graphics.Color.argb(220, 239, 68, 68)
        textSize = 8f * density
        typeface = Typeface.MONOSPACE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    drawContext.canvas.nativeCanvas.drawText(warningText, centerPos.x, centerPos.y, paint)
}

private fun DrawScope.drawTacticalBorderRulers(
    canvasW: Float,
    canvasH: Float,
    zoom: Float,
    pan: Offset,
    is3D: Boolean,
    density: Float
) {
    val pTL = mapToScreenCoordinates(0f, 0f, 0, canvasW, canvasH, zoom, pan, is3D, 0)
    val pTR = mapToScreenCoordinates(1000f, 0f, 0, canvasW, canvasH, zoom, pan, is3D, 0)
    val pBL = mapToScreenCoordinates(0f, 1000f, 0, canvasW, canvasH, zoom, pan, is3D, 0)

    val rulerPaint = Paint().apply {
        color = android.graphics.Color.argb(160, 148, 163, 184)
        textSize = 9f * density
        typeface = Typeface.MONOSPACE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    // Top horizontal coordinate ticks (01..09)
    for (i in 1..9) {
        val gx = i * 100f
        val pos = mapToScreenCoordinates(gx, 0f, 0, canvasW, canvasH, zoom, pan, is3D, 0)
        drawContext.canvas.nativeCanvas.drawText("0$i", pos.x, pos.y - (6f * density), rulerPaint)
        drawLine(Color(0xFF64748B), pos, Offset(pos.x, pos.y - (4f * density)), 1.5f * density)
    }

    // Left vertical coordinate ticks (51..59)
    for (i in 1..9) {
        val gy = i * 100f
        val pos = mapToScreenCoordinates(0f, gy, 0, canvasW, canvasH, zoom, pan, is3D, 0)
        drawContext.canvas.nativeCanvas.drawText("5$i", pos.x - (12f * density), pos.y + (3f * density), rulerPaint)
        drawLine(Color(0xFF64748B), pos, Offset(pos.x - (4f * density), pos.y), 1.5f * density)
    }

    // Mil-spec Scale bar in bottom left
    val scaleP1 = Offset(pBL.x + 10f * density, pBL.y - 12f * density)
    val scaleP2 = Offset(scaleP1.x + 60f * density, scaleP1.y)
    drawLine(TacticalAmber, scaleP1, scaleP2, 2f * density)
    drawLine(TacticalAmber, scaleP1, Offset(scaleP1.x, scaleP1.y - 4f * density), 2f * density)
    drawLine(TacticalAmber, scaleP2, Offset(scaleP2.x, scaleP2.y - 4f * density), 2f * density)

    val scalePaint = Paint().apply {
        color = TacticalAmber.toArgb()
        textSize = 8f * density
        typeface = Typeface.MONOSPACE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    drawContext.canvas.nativeCanvas.drawText("100M SCALE", (scaleP1.x + scaleP2.x) / 2f, scaleP1.y - 4f * density, scalePaint)
}

private fun DrawScope.drawVectorFeature(
    feature: com.example.model.MapVectorFeature,
    canvasW: Float,
    canvasH: Float,
    zoom: Float,
    pan: Offset,
    is3D: Boolean,
    selectedFloor: Int?,
    density: Float,
    overlayMode: MapOverlayMode = MapOverlayMode.SATELLITE
) {
    if (feature.points.size < 2) return

    val path = Path()
    val firstScreen = mapToScreenCoordinates(
        feature.points[0].first, feature.points[0].second, 0,
        canvasW, canvasH, zoom, pan, is3D, selectedFloor
    )
    path.moveTo(firstScreen.x, firstScreen.y)

    for (i in 1 until feature.points.size) {
        val pt = mapToScreenCoordinates(
            feature.points[i].first, feature.points[i].second, 0,
            canvasW, canvasH, zoom, pan, is3D, selectedFloor
        )
        path.lineTo(pt.x, pt.y)
    }

    when (feature.type) {
        VectorFeatureType.ROAD -> {
            // Road asphalt casing
            drawPath(
                path = path,
                color = Color(0xFF1E2530).copy(alpha = if (overlayMode == MapOverlayMode.SATELLITE) 0.65f else 0.95f),
                style = Stroke(width = (14f * zoom * density).coerceIn(4f, 32f))
            )
            // Road driving surface
            drawPath(
                path = path,
                color = Color(0xFF374151).copy(alpha = if (overlayMode == MapOverlayMode.SATELLITE) 0.55f else 0.90f),
                style = Stroke(width = (10f * zoom * density).coerceIn(3f, 24f))
            )
            // Yellow center dashed lane divider
            drawPath(
                path = path,
                color = Color(0xFFFBBF24).copy(alpha = 0.70f),
                style = Stroke(
                    width = (1.5f * density).coerceAtLeast(1f),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )
            )
        }
        VectorFeatureType.WATER -> {
            // Water deep channel
            drawPath(
                path = path,
                color = Color(0xFF0F3B66).copy(alpha = if (overlayMode == MapOverlayMode.SATELLITE) 0.60f else 0.90f),
                style = Stroke(width = (24f * zoom * density).coerceIn(6f, 48f))
            )
            // Shoreline surf/wave rim
            drawPath(
                path = path,
                color = Color(0xFF38BDF8).copy(alpha = 0.65f),
                style = Stroke(
                    width = (2.5f * density).coerceAtLeast(1.5f),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 8f), 0f)
                )
            )
        }
        VectorFeatureType.RAILROAD -> {
            // Steel rails
            drawPath(
                path = path,
                color = Color(0xFF94A3B8),
                style = Stroke(width = (3.5f * density).coerceIn(2f, 8f))
            )
            // Crossbar railway ties
            drawPath(
                path = path,
                color = Color(0xFF64748B),
                style = Stroke(
                    width = (8f * density).coerceIn(3f, 16f),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 6f), 0f)
                )
            )
        }
        VectorFeatureType.BUILDING_OUTLINE -> {
            drawPath(
                path = path,
                color = TacticalAmber.copy(alpha = if (overlayMode == MapOverlayMode.SATELLITE) 0.70f else 0.85f),
                style = Stroke(width = (2.5f * density).coerceIn(1.5f, 6f))
            )
        }
        VectorFeatureType.STRUCTURE_INTERIOR -> {
            drawPath(
                path = path,
                color = TacticalCyan.copy(alpha = if (overlayMode == MapOverlayMode.SATELLITE) 0.60f else 0.75f),
                style = Stroke(
                    width = (1.8f * density).coerceIn(1f, 4f),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )
            )
        }
        VectorFeatureType.FENCE -> {
            drawPath(
                path = path,
                color = Color(0xFFA0AEC0).copy(alpha = 0.8f),
                style = Stroke(
                    width = 1.5f * density,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                )
            )
        }
        VectorFeatureType.HAZARD_ZONE -> {
            drawPath(
                path = path,
                color = TacticalHazardRed.copy(alpha = 0.8f),
                style = Stroke(
                    width = (3f * density).coerceIn(2f, 8f),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                )
            )
        }
    }
}

private fun DrawScope.drawStructure(
    structure: MapStructure,
    floors: List<FloorLevel>,
    canvasW: Float,
    canvasH: Float,
    zoom: Float,
    pan: Offset,
    is3D: Boolean,
    selectedFloor: Int?,
    density: Float,
    showLabels: Boolean,
    overlayMode: MapOverlayMode = MapOverlayMode.SATELLITE
) {
    val floorListToDraw = if (selectedFloor != null) {
        structure.floors.filter { it.floorIndex <= selectedFloor }
    } else {
        structure.floors
    }

    floorListToDraw.forEach { fl ->
        val isActive = selectedFloor == null || fl.floorIndex == selectedFloor

        // In satellite mode: keep the building fill transparent so user sees satellite roof underneath!
        val alphaFill = when {
            overlayMode == MapOverlayMode.SATELLITE -> if (isActive) 0.18f else 0.08f
            overlayMode == MapOverlayMode.TACTICAL_HYBRID -> if (isActive) 0.45f else 0.20f
            else -> if (isActive) 0.85f else 0.35f
        }

        val fillColor = if (isActive) Color(0xFF1E2D3F) else Color(0xFF141C26)
        val strokeColor = if (isActive) TacticalAmber else Color(0xFF475569)
        val strokeWidth = if (isActive) (2.2f * density) else (1.2f * density)

        val pTopLeft = mapToScreenCoordinates(structure.left, structure.top, fl.floorIndex, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
        val pTopRight = mapToScreenCoordinates(structure.right, structure.top, fl.floorIndex, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
        val pBottomRight = mapToScreenCoordinates(structure.right, structure.bottom, fl.floorIndex, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
        val pBottomLeft = mapToScreenCoordinates(structure.left, structure.bottom, fl.floorIndex, canvasW, canvasH, zoom, pan, is3D, selectedFloor)

        val polyPath = Path().apply {
            moveTo(pTopLeft.x, pTopLeft.y)
            lineTo(pTopRight.x, pTopRight.y)
            lineTo(pBottomRight.x, pBottomRight.y)
            lineTo(pBottomLeft.x, pBottomLeft.y)
            close()
        }

        // 1. Floor plan / roof surface fill
        drawPath(
            path = polyPath,
            color = fillColor.copy(alpha = alphaFill),
            style = Fill
        )

        // 2. High-contrast building perimeter walls
        drawPath(
            path = polyPath,
            color = strokeColor,
            style = Stroke(width = strokeWidth)
        )

        // 3. Tactical Corner Reticles (L-shaped target designation brackets)
        if (isActive) {
            val bracketLen = (8f * density).coerceAtLeast(4f)
            // Top-left
            drawLine(strokeColor, pTopLeft, Offset(pTopLeft.x + bracketLen, pTopLeft.y), strokeWidth * 1.5f)
            drawLine(strokeColor, pTopLeft, Offset(pTopLeft.x, pTopLeft.y + bracketLen), strokeWidth * 1.5f)
            // Top-right
            drawLine(strokeColor, pTopRight, Offset(pTopRight.x - bracketLen, pTopRight.y), strokeWidth * 1.5f)
            drawLine(strokeColor, pTopRight, Offset(pTopRight.x, pTopRight.y + bracketLen), strokeWidth * 1.5f)
            // Bottom-right
            drawLine(strokeColor, pBottomRight, Offset(pBottomRight.x - bracketLen, pBottomRight.y), strokeWidth * 1.5f)
            drawLine(strokeColor, pBottomRight, Offset(pBottomRight.x, pBottomRight.y - bracketLen), strokeWidth * 1.5f)
            // Bottom-left
            drawLine(strokeColor, pBottomLeft, Offset(pBottomLeft.x + bracketLen, pBottomLeft.y), strokeWidth * 1.5f)
            drawLine(strokeColor, pBottomLeft, Offset(pBottomLeft.x, pBottomLeft.y - bracketLen), strokeWidth * 1.5f)
        }

        // 4. Interior tactical grid lines inside building
        if (overlayMode != MapOverlayMode.SATELLITE || !isActive) {
            val pMidTop = Offset((pTopLeft.x + pTopRight.x) / 2, (pTopLeft.y + pTopRight.y) / 2)
            val pMidBottom = Offset((pBottomLeft.x + pBottomRight.x) / 2, (pBottomLeft.y + pBottomRight.y) / 2)
            val pMidLeft = Offset((pTopLeft.x + pBottomLeft.x) / 2, (pTopLeft.y + pBottomLeft.y) / 2)
            val pMidRight = Offset((pTopRight.x + pBottomRight.x) / 2, (pTopRight.y + pBottomRight.y) / 2)

            drawLine(
                color = strokeColor.copy(alpha = if (isActive) 0.35f else 0.15f),
                start = pMidTop,
                end = pMidBottom,
                strokeWidth = 1f * density,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
            )
            drawLine(
                color = strokeColor.copy(alpha = if (isActive) 0.35f else 0.15f),
                start = pMidLeft,
                end = pMidRight,
                strokeWidth = 1f * density,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
            )
        }

        // 5. Connecting vertical 3D axonometric pillars
        if (is3D && selectedFloor == null && fl.floorIndex > -1) {
            val prevP1 = mapToScreenCoordinates(structure.left, structure.top, fl.floorIndex - 1, canvasW, canvasH, zoom, pan, is3D, selectedFloor)
            drawLine(
                color = TacticalAmber.copy(alpha = 0.45f),
                start = prevP1,
                end = pTopLeft,
                strokeWidth = 1.2f * density,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )
        }
    }

    // 6. Render Structure Name Label
    if (showLabels) {
        val centerMapX = (structure.left + structure.right) / 2
        val centerMapY = (structure.top + structure.bottom) / 2
        val centerPos = mapToScreenCoordinates(centerMapX, centerMapY, selectedFloor ?: 0, canvasW, canvasH, zoom, pan, is3D, selectedFloor)

        drawPillText(
            text = structure.name.uppercase(),
            x = centerPos.x,
            y = centerPos.y,
            textColor = TacticalAmber.toArgb(),
            bgColor = android.graphics.Color.argb(220, 15, 23, 42),
            borderColor = TacticalAmber.toArgb(),
            fontSize = 9.5f * density,
            density = density
        )
    }
}

private fun DrawScope.drawLockedDoorMarker(
    lock: LockedDoorMarker,
    pos: Offset,
    isFloorActive: Boolean,
    showLabels: Boolean,
    isHighlighted: Boolean,
    pulseRadius: Float,
    density: Float
) {
    val alpha = if (isFloorActive) 1.0f else 0.45f
    val baseRadius = 14f * density
    val baseColor = if (lock.isMarkedRoom) Color(0xFFA855F7) else TacticalAmber

    // Glowing aura for Marked Rooms or highlighted doors
    if (lock.isMarkedRoom || isHighlighted) {
        drawCircle(
            color = baseColor.copy(alpha = 0.45f * alpha),
            radius = baseRadius + pulseRadius * density,
            center = pos
        )
    }

    // Outer octagonal / rounded badge
    drawRoundRect(
        color = Color(0xFF0F172A).copy(alpha = alpha),
        topLeft = Offset(pos.x - baseRadius, pos.y - baseRadius),
        size = Size(baseRadius * 2, baseRadius * 2),
        cornerRadius = CornerRadius(5f * density, 5f * density)
    )

    drawRoundRect(
        color = baseColor.copy(alpha = alpha),
        topLeft = Offset(pos.x - baseRadius, pos.y - baseRadius),
        size = Size(baseRadius * 2, baseRadius * 2),
        cornerRadius = CornerRadius(5f * density, 5f * density),
        style = Stroke(width = 2.5f * density)
    )

    // Key badge icon / text "KEY" or "★MK" for marked room
    val textPaint = Paint().apply {
        color = baseColor.toArgb()
        textSize = 8.5f * density
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    val badgeLabel = if (lock.isMarkedRoom) "★MK" else "KEY"
    drawContext.canvas.nativeCanvas.drawText(badgeLabel, pos.x, pos.y + 3f * density, textPaint)

    if (showLabels && isFloorActive) {
        val labelPrefix = if (lock.isMarkedRoom) "✦ " else "🔑 "
        drawPillText(
            text = "$labelPrefix${lock.doorName}",
            x = pos.x,
            y = pos.y + baseRadius + (8f * density),
            textColor = baseColor.toArgb(),
            bgColor = android.graphics.Color.argb(235, 15, 23, 42),
            borderColor = baseColor.toArgb(),
            fontSize = 9.5f * density,
            density = density
        )
    }
}

private fun DrawScope.drawLootMarker(
    loot: LootSpawn,
    pos: Offset,
    isFloorActive: Boolean,
    showLabels: Boolean,
    isHighlighted: Boolean,
    pulseRadius: Float,
    density: Float
) {
    val alpha = if (isFloorActive) 1.0f else 0.45f
    val isContainer = loot.category == LootCategory.CONTAINER ||
            loot.containerType in listOf("Safe", "PC Block", "Medcase", "Weapon Box", "Jacket", "Toolbox", "Dead Scav", "Duffle Bag", "Grenade Box")

    val containerColor = when {
        loot.containerType.contains("Safe", ignoreCase = true) -> Color(0xFFF59E0B)
        loot.containerType.contains("PC", ignoreCase = true) -> Color(0xFF38BDF8)
        loot.containerType.contains("Med", ignoreCase = true) -> Color(0xFFEF4444)
        loot.containerType.contains("Weapon", ignoreCase = true) -> Color(0xFF10B981)
        loot.containerType.contains("Jacket", ignoreCase = true) -> Color(0xFFA855F7)
        loot.containerType.contains("Tool", ignoreCase = true) -> Color(0xFFF97316)
        loot.containerType.contains("Scav", ignoreCase = true) -> Color(0xFF94A3B8)
        loot.containerType.contains("Duffle", ignoreCase = true) -> Color(0xFF64748B)
        loot.containerType.contains("Grenade", ignoreCase = true) -> Color(0xFFEAB308)
        else -> loot.rarity.color
    }

    val containerLabel = when {
        loot.containerType.contains("Safe", ignoreCase = true) -> "SAFE"
        loot.containerType.contains("PC", ignoreCase = true) -> "PC"
        loot.containerType.contains("Med", ignoreCase = true) -> "MED"
        loot.containerType.contains("Weapon", ignoreCase = true) -> "BOX"
        loot.containerType.contains("Jacket", ignoreCase = true) -> "JKT"
        loot.containerType.contains("Tool", ignoreCase = true) -> "TOOL"
        loot.containerType.contains("Scav", ignoreCase = true) -> "SCAV"
        loot.containerType.contains("Duffle", ignoreCase = true) -> "BAG"
        loot.containerType.contains("Grenade", ignoreCase = true) -> "NADE"
        else -> loot.rarity.tier
    }

    val baseRadius = when (loot.rarity) {
        Rarity.LEGENDARY -> 16f * density
        Rarity.EPIC -> 13f * density
        Rarity.RARE -> 11f * density
        Rarity.COMMON -> 9.5f * density
    }

    // Glowing animated pulse for high-tier or highlighted loot
    if (loot.rarity == Rarity.LEGENDARY || isHighlighted) {
        drawCircle(
            color = (if (isContainer) containerColor else loot.rarity.color).copy(alpha = (0.4f * alpha)),
            radius = baseRadius + pulseRadius * density,
            center = pos
        )
    }

    if (isContainer) {
        // High-contrast container box shape (Tarkov.dev style)
        val boxSize = baseRadius * 1.8f
        drawRoundRect(
            color = Color(0xFF0F172A).copy(alpha = alpha),
            topLeft = Offset(pos.x - boxSize / 2, pos.y - boxSize / 2),
            size = Size(boxSize, boxSize),
            cornerRadius = CornerRadius(4f * density, 4f * density)
        )
        drawRoundRect(
            color = containerColor.copy(alpha = alpha),
            topLeft = Offset(pos.x - boxSize / 2, pos.y - boxSize / 2),
            size = Size(boxSize, boxSize),
            cornerRadius = CornerRadius(4f * density, 4f * density),
            style = Stroke(width = 2.2f * density)
        )
        // Inner badge label
        val cPaint = Paint().apply {
            color = containerColor.toArgb()
            textSize = 7.5f * density
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        drawContext.canvas.nativeCanvas.drawText(
            containerLabel,
            pos.x,
            pos.y + 2.5f * density,
            cPaint
        )
    } else {
        // Standard loose loot circular badge
        drawCircle(
            color = Color(0xFF0F172A).copy(alpha = alpha),
            radius = baseRadius,
            center = pos
        )

        drawCircle(
            color = loot.rarity.color.copy(alpha = alpha),
            radius = baseRadius,
            center = pos,
            style = Stroke(width = 2.5f * density)
        )

        drawCircle(
            color = loot.rarity.color.copy(alpha = alpha),
            radius = baseRadius * 0.6f,
            center = pos
        )

        val letterPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = (baseRadius * 0.9f)
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        drawContext.canvas.nativeCanvas.drawText(
            loot.rarity.tier,
            pos.x,
            pos.y + (baseRadius * 0.32f),
            letterPaint
        )
    }

    // Draw text label with dark protective pill
    if (showLabels && isFloorActive) {
        val labelColor = if (isContainer) containerColor else loot.rarity.color
        val labelText = if (loot.rarity == Rarity.LEGENDARY) "★ ${loot.name}" else loot.name
        drawPillText(
            text = labelText,
            x = pos.x,
            y = pos.y + baseRadius + (8f * density),
            textColor = labelColor.toArgb(),
            bgColor = android.graphics.Color.argb(225, 12, 17, 24),
            borderColor = labelColor.copy(alpha = 0.5f).toArgb(),
            fontSize = 9.5f * density,
            density = density
        )
    }
}

private fun DrawScope.drawExtractMarker(
    extract: ExtractionPoint,
    pos: Offset,
    isFloorActive: Boolean,
    showLabels: Boolean,
    isHighlighted: Boolean,
    pulseRadius: Float,
    density: Float
) {
    val alpha = if (isFloorActive) 1.0f else 0.5f
    val color = extract.factionColor.copy(alpha = alpha)
    val width = 30f * density
    val height = 22f * density

    // Pulsing halo for extracts
    if (isHighlighted || extract.hasGreenSmoke) {
        drawCircle(
            color = TacticalEmerald.copy(alpha = 0.35f * alpha),
            radius = width * 0.8f + pulseRadius * density,
            center = pos
        )
    }

    // High-visibility rounded tactical shield for exfil
    val rectLeft = pos.x - width / 2
    val rectTop = pos.y - height / 2

    drawRoundRect(
        color = Color(0xFF0F172A).copy(alpha = alpha),
        topLeft = Offset(rectLeft, rectTop),
        size = Size(width, height),
        cornerRadius = CornerRadius(4f * density, 4f * density)
    )

    drawRoundRect(
        color = color,
        topLeft = Offset(rectLeft, rectTop),
        size = Size(width, height),
        cornerRadius = CornerRadius(4f * density, 4f * density),
        style = Stroke(width = 2.2f * density)
    )

    // EXFIL Icon / Text inside badge
    val badgePaint = Paint().apply {
        this.color = color.toArgb()
        textSize = 9f * density
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    drawContext.canvas.nativeCanvas.drawText("EXFIL", pos.x, pos.y + 3f * density, badgePaint)

    // Name label
    if (showLabels) {
        val floorTag = if (extract.floorIndex == 0) "" else " (${extract.floorIndex}F)"
        drawPillText(
            text = "${extract.name}$floorTag",
            x = pos.x,
            y = pos.y - (height / 2) - (8f * density),
            textColor = android.graphics.Color.WHITE,
            bgColor = android.graphics.Color.argb(230, 6, 78, 59), // deep emerald
            borderColor = TacticalEmerald.toArgb(),
            fontSize = 9.5f * density,
            density = density
        )
    }
}

private fun DrawScope.drawBossMarker(
    boss: BossSpawn,
    pos: Offset,
    isFloorActive: Boolean,
    showLabels: Boolean,
    isHighlighted: Boolean,
    pulseRadius: Float,
    density: Float
) {
    val alpha = if (isFloorActive) 1.0f else 0.5f
    val size = 32f * density

    // Hazard pulse
    drawCircle(
        color = TacticalHazardRed.copy(alpha = 0.35f * alpha),
        radius = (size / 2) + pulseRadius * density,
        center = pos
    )

    // Square hazard icon
    drawRoundRect(
        color = Color(0xFF450A0A).copy(alpha = alpha),
        topLeft = Offset(pos.x - size / 2, pos.y - size / 2),
        size = Size(size, size),
        cornerRadius = CornerRadius(4f * density, 4f * density)
    )

    drawRoundRect(
        color = TacticalHazardRed.copy(alpha = alpha),
        topLeft = Offset(pos.x - size / 2, pos.y - size / 2),
        size = Size(size, size),
        cornerRadius = CornerRadius(4f * density, 4f * density),
        style = Stroke(width = 2.5f * density)
    )

    // Crosshairs
    drawLine(
        color = Color.White.copy(alpha = alpha),
        start = Offset(pos.x - 7f * density, pos.y),
        end = Offset(pos.x + 7f * density, pos.y),
        strokeWidth = 2f * density
    )
    drawLine(
        color = Color.White.copy(alpha = alpha),
        start = Offset(pos.x, pos.y - 7f * density),
        end = Offset(pos.x, pos.y + 7f * density),
        strokeWidth = 2f * density
    )

    if (showLabels) {
        drawPillText(
            text = "⚠ BOSS: ${boss.name.uppercase()}",
            x = pos.x,
            y = pos.y + (size / 2) + (8f * density),
            textColor = TacticalHazardRed.toArgb(),
            bgColor = android.graphics.Color.argb(230, 69, 10, 10),
            borderColor = TacticalHazardRed.toArgb(),
            fontSize = 9.5f * density,
            density = density
        )
    }
}

private fun DrawScope.drawQuestMarker(
    quest: QuestMarker,
    pos: Offset,
    isFloorActive: Boolean,
    showLabels: Boolean,
    density: Float
) {
    val alpha = if (isFloorActive) 1.0f else 0.5f
    val radius = 12f * density

    drawCircle(
        color = Color(0xFF0F172A).copy(alpha = alpha),
        radius = radius,
        center = pos
    )

    drawCircle(
        color = TacticalAmber.copy(alpha = alpha),
        radius = radius,
        center = pos,
        style = Stroke(width = 2f * density)
    )

    drawCircle(
        color = TacticalAmber.copy(alpha = alpha),
        radius = radius * 0.45f,
        center = pos
    )

    if (showLabels && isFloorActive) {
        drawPillText(
            text = "◆ ${quest.questName}",
            x = pos.x,
            y = pos.y + radius + (8f * density),
            textColor = TacticalAmber.toArgb(),
            bgColor = android.graphics.Color.argb(220, 26, 20, 10),
            borderColor = TacticalAmber.toArgb(),
            fontSize = 9f * density,
            density = density
        )
    }
}

private fun DrawScope.drawUserPinMarker(
    pin: CustomPinEntity,
    pos: Offset,
    isFloorActive: Boolean,
    showLabels: Boolean,
    density: Float
) {
    val alpha = if (isFloorActive) 1.0f else 0.5f
    val radius = 13f * density

    drawCircle(
        color = TacticalCyan.copy(alpha = alpha),
        radius = radius,
        center = pos
    )

    drawCircle(
        color = Color.White.copy(alpha = alpha),
        radius = 5f * density,
        center = pos
    )

    if (showLabels && isFloorActive) {
        drawPillText(
            text = "📍 ${pin.title}",
            x = pos.x,
            y = pos.y + radius + (8f * density),
            textColor = TacticalCyan.toArgb(),
            bgColor = android.graphics.Color.argb(230, 8, 47, 73),
            borderColor = TacticalCyan.toArgb(),
            fontSize = 9.5f * density,
            density = density
        )
    }
}

private fun DrawScope.drawPillText(
    text: String,
    x: Float,
    y: Float,
    textColor: Int,
    bgColor: Int,
    borderColor: Int,
    fontSize: Float,
    density: Float
) {
    val textPaint = Paint().apply {
        color = textColor
        textSize = fontSize
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    val bounds = Rect()
    textPaint.getTextBounds(text, 0, text.length, bounds)

    val paddingH = 6f * density
    val paddingV = 3f * density

    val rectLeft = x - (bounds.width() / 2f) - paddingH
    val rectRight = x + (bounds.width() / 2f) + paddingH
    val rectTop = y - bounds.height() - paddingV
    val rectBottom = y + paddingV

    val bgPaint = Paint().apply {
        color = bgColor
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    val borderPaint = Paint().apply {
        color = borderColor
        style = Paint.Style.STROKE
        strokeWidth = 1.2f * density
        isAntiAlias = true
    }

    val cornerRadius = 4f * density
    val rectF = android.graphics.RectF(rectLeft, rectTop, rectRight, rectBottom)

    drawContext.canvas.nativeCanvas.drawRoundRect(rectF, cornerRadius, cornerRadius, bgPaint)
    drawContext.canvas.nativeCanvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)
    drawContext.canvas.nativeCanvas.drawText(text, x, y - (paddingV / 2), textPaint)
}
