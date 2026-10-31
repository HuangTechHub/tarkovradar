package com.example.ui.map

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
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
    showGrid: Boolean,
    showLabels: Boolean,
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

            // 1. Draw Military Blueprint Ground Grid & Coordinate Borders
            drawTacticalGround(canvasW, canvasH, zoomScale, panOffset, is3DMode, showGrid, density)

            // 2. Draw Vector Topography Features (Roads, Water, Rail, Fences)
            map.vectorFeatures.forEach { feature ->
                drawVectorFeature(feature, canvasW, canvasH, zoomScale, panOffset, is3DMode, selectedFloorIndex, density)
            }

            // 3. Draw Structures, Floor Footprints & Room Partitions
            map.structures.forEach { structure ->
                drawStructure(structure, map.floors, canvasW, canvasH, zoomScale, panOffset, is3DMode, selectedFloorIndex, density, showLabels)
            }

            // 4. Project and Draw Markers
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

            // D. Loot Markers
            map.lootSpawns.forEach { loot ->
                if (rarityFilter.contains(loot.rarity) && categoryFilter.contains(loot.category)) {
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

            // E. Custom User Pins
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
        color = Color(0xFF151C26), // distinct military slate
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
        val gridColor = Color(0xFF263342).copy(alpha = 0.75f)

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

        // Draw Sector letters on corners
        val sectorPaint = Paint().apply {
            color = android.graphics.Color.argb(140, 148, 163, 184)
            textSize = 10f * density
            typeface = Typeface.MONOSPACE
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        listOf(
            Triple("SEC A", 200f, 200f),
            Triple("SEC B", 800f, 200f),
            Triple("SEC C", 500f, 500f),
            Triple("SEC D", 200f, 800f),
            Triple("SEC E", 800f, 800f)
        ).forEach { (label, sx, sy) ->
            val spos = mapToScreenCoordinates(sx, sy, 0, canvasW, canvasH, zoom, pan, is3D, 0)
            drawContext.canvas.nativeCanvas.drawText(label, spos.x, spos.y, sectorPaint)
        }
    }
}

private fun DrawScope.drawVectorFeature(
    feature: com.example.model.MapVectorFeature,
    canvasW: Float,
    canvasH: Float,
    zoom: Float,
    pan: Offset,
    is3D: Boolean,
    selectedFloor: Int?,
    density: Float
) {
    if (feature.points.size < 2) return

    val color = when (feature.type) {
        VectorFeatureType.WATER -> Color(0xFF1E3A8A).copy(alpha = 0.85f)
        VectorFeatureType.ROAD -> Color(0xFF3B4A5D)
        VectorFeatureType.RAILROAD -> Color(0xFF718096)
        VectorFeatureType.BUILDING_OUTLINE -> TacticalAmber.copy(alpha = 0.45f)
        VectorFeatureType.STRUCTURE_INTERIOR -> TacticalCyan.copy(alpha = 0.35f)
        VectorFeatureType.FENCE -> Color(0xFFA0AEC0)
        VectorFeatureType.HAZARD_ZONE -> TacticalHazardRed.copy(alpha = 0.4f)
    }

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

    val width = when (feature.type) {
        VectorFeatureType.ROAD -> 10f * zoom * density
        VectorFeatureType.WATER -> 22f * zoom * density
        VectorFeatureType.RAILROAD -> 5f * zoom * density
        VectorFeatureType.BUILDING_OUTLINE -> 3f * zoom * density
        else -> 2f * density
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = width.coerceIn(2f, 28f))
    )
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
    showLabels: Boolean
) {
    val floorListToDraw = if (selectedFloor != null) {
        structure.floors.filter { it.floorIndex <= selectedFloor }
    } else {
        structure.floors
    }

    floorListToDraw.forEach { fl ->
        val isActive = selectedFloor == null || fl.floorIndex == selectedFloor
        val alphaFill = if (isActive) 0.85f else 0.35f

        // High-contrast building colors
        val fillColor = if (isActive) Color(0xFF1E2D3F) else Color(0xFF141C26)
        val strokeColor = if (isActive) TacticalAmber else Color(0xFF33455A)
        val strokeWidth = if (isActive) (2.5f * density) else (1.2f * density)

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

        // 1. Solid floor plan surface
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

        // 3. Interior tactical grid lines inside building
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

        // 4. Connecting vertical 3D axonometric pillars
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

    // 5. Render Structure Name Label
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
            borderColor = TacticalAmber.copy(alpha = 0.6f).toArgb(),
            fontSize = 10f * density,
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
    val baseRadius = when (loot.rarity) {
        Rarity.LEGENDARY -> 16f * density
        Rarity.EPIC -> 13f * density
        Rarity.RARE -> 11f * density
        Rarity.COMMON -> 9f * density
    }

    // Glowing animated pulse for high-tier or highlighted loot
    if (loot.rarity == Rarity.LEGENDARY || isHighlighted) {
        drawCircle(
            color = loot.rarity.color.copy(alpha = (0.4f * alpha)),
            radius = baseRadius + pulseRadius * density,
            center = pos
        )
    }

    // Outer badge ring
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

    // Inner filled color core
    drawCircle(
        color = loot.rarity.color.copy(alpha = alpha),
        radius = baseRadius * 0.6f,
        center = pos
    )

    // Draw Rarity Letter inside marker badge ('S', 'A', 'B', 'C')
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

    // Draw text label with dark protective pill
    if (showLabels && isFloorActive) {
        val labelText = if (loot.rarity == Rarity.LEGENDARY) "★ ${loot.name}" else loot.name
        drawPillText(
            text = labelText,
            x = pos.x,
            y = pos.y + baseRadius + (8f * density),
            textColor = loot.rarity.color.toArgb(),
            bgColor = android.graphics.Color.argb(225, 12, 17, 24),
            borderColor = loot.rarity.color.copy(alpha = 0.5f).toArgb(),
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
