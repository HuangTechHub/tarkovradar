package com.example.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LabelOff
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ViewInAr
import com.example.model.MapOverlayMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PrivacyPolicyDialog
import com.example.ui.hud.ExtractChecklistDialog
import com.example.ui.hud.LootFilterChips
import com.example.ui.hud.RaidTimerBar
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.TarkovBackground
import com.example.ui.theme.TarkovSurface
import com.example.ui.theme.TarkovSurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TarkovRadarViewModel

@Composable
fun TacticalMapScreen(
    viewModel: TarkovRadarViewModel,
    modifier: Modifier = Modifier
) {
    val selectedMap by viewModel.selectedMap.collectAsState()
    val selectedFloorIndex by viewModel.selectedFloorIndex.collectAsState()
    val is3DMode by viewModel.is3DMode.collectAsState()
    val zoomScale by viewModel.zoomScale.collectAsState()
    val panOffset by viewModel.panOffset.collectAsState()
    val showLabels by viewModel.showLabels.collectAsState()
    val highlightMarkerId by viewModel.highlightMarkerId.collectAsState()

    val selectedRarities by viewModel.selectedRarities.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val showExtracts by viewModel.showExtracts.collectAsState()
    val showBosses by viewModel.showBosses.collectAsState()
    val showQuests by viewModel.showQuests.collectAsState()
    val showLockedDoors by viewModel.showLockedDoors.collectAsState()
    val showContainers by viewModel.showContainers.collectAsState()
    val selectedContainerType by viewModel.selectedContainerType.collectAsState()
    val showGrid by viewModel.showGrid.collectAsState()

    val customPins by viewModel.customPins.collectAsState()
    val selectedMarker by viewModel.selectedMarker.collectAsState()
    val showFullDetailDialog by viewModel.showFullDetailDialog.collectAsState()
    val dropPinCoords by viewModel.dropPinCoords.collectAsState()
    val showExtractChecklist by viewModel.showExtractChecklist.collectAsState()
    val showMapPicker by viewModel.showMapPicker.collectAsState()
    val showPoiDirectory by viewModel.showPoiDirectory.collectAsState()
    val showPrivacyPolicy by viewModel.showPrivacyPolicy.collectAsState()
    val visitedExtractIds by viewModel.visitedExtractIds.collectAsState()

    val remainingSeconds by viewModel.raidRemainingSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val overlayMode by viewModel.overlayMode.collectAsState()
    val satelliteOpacity by viewModel.satelliteOpacity.collectAsState()

    // Calculate count of POIs per floor so the floor ladder shows item counts
    val floorPoiCounts = remember(selectedMap, customPins) {
        val mapCounts = mutableMapOf<Int, Int>()
        selectedMap.floors.forEach { mapCounts[it.floorIndex] = 0 }

        selectedMap.lootSpawns.forEach { loot ->
            mapCounts[loot.floorIndex] = (mapCounts[loot.floorIndex] ?: 0) + 1
        }
        selectedMap.extractions.forEach { ex ->
            mapCounts[ex.floorIndex] = (mapCounts[ex.floorIndex] ?: 0) + 1
        }
        selectedMap.bosses.forEach { boss ->
            mapCounts[boss.floorIndex] = (mapCounts[boss.floorIndex] ?: 0) + 1
        }
        selectedMap.lockedDoors.forEach { lock ->
            mapCounts[lock.floorIndex] = (mapCounts[lock.floorIndex] ?: 0) + 1
        }
        mapCounts
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TarkovBackground)
            .testTag("tactical_map_screen")
    ) {
        // 1. High-Contrast Tactical Blueprint / Satellite Canvas
        MapCanvasView(
            map = selectedMap,
            is3DMode = is3DMode,
            selectedFloorIndex = selectedFloorIndex,
            rarityFilter = selectedRarities,
            categoryFilter = selectedCategory?.let { setOf(it) } ?: com.example.model.LootCategory.values().toSet(),
            showExtracts = showExtracts,
            showBosses = showBosses,
            showQuests = showQuests,
            showLockedDoors = showLockedDoors,
            showContainers = showContainers,
            selectedContainerType = selectedContainerType,
            showGrid = showGrid,
            showLabels = showLabels,
            overlayMode = overlayMode,
            satelliteOpacity = satelliteOpacity,
            customPins = customPins,
            highlightMarkerId = highlightMarkerId,
            zoomScale = zoomScale,
            panOffset = panOffset,
            onZoomChange = { viewModel.setZoomScale(it) },
            onPanChange = { viewModel.setPanOffset(it) },
            onMarkerSelected = { marker -> viewModel.selectMarker(marker) },
            onMapLongPress = { x, y, floor -> viewModel.initiateDropPin(x, y, floor) }
        )

        // 2. Top HUD Controls: Raid Timer & Filter Strip
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            RaidTimerBar(
                mapName = selectedMap.name,
                remainingSeconds = remainingSeconds,
                isRunning = isTimerRunning,
                onToggleTimer = { viewModel.toggleRaidTimer() },
                onResetTimer = { viewModel.resetRaidTimer() },
                onOpenExtractList = { viewModel.openExtractChecklist() },
                onOpenMapPicker = { viewModel.openMapPicker() }
            )

            LootFilterChips(
                selectedRarities = selectedRarities,
                onToggleRarity = { viewModel.toggleRarity(it) },
                onResetRarities = { viewModel.resetRarities() },
                selectedCategory = selectedCategory,
                onSelectCategory = { viewModel.selectCategory(it) },
                showExtracts = showExtracts,
                onToggleExtracts = { viewModel.toggleExtracts() },
                showBosses = showBosses,
                onToggleBosses = { viewModel.toggleBosses() },
                showLockedDoors = showLockedDoors,
                onToggleLockedDoors = { viewModel.toggleLockedDoors() },
                showContainers = showContainers,
                onToggleContainers = { viewModel.toggleContainers() },
                selectedContainerType = selectedContainerType,
                onSelectContainerType = { viewModel.selectContainerType(it) },
                is3DMode = is3DMode,
                onToggle3DMode = { viewModel.toggle3DMode() },
                overlayMode = overlayMode,
                onToggleOverlayMode = { viewModel.toggleOverlayMode() }
            )
        }

        // 3. Right Side: Floor Elevation Selector Ladder
        if (selectedMap.floors.size > 1) {
            FloorElevationSelector(
                floors = selectedMap.floors,
                selectedFloorIndex = selectedFloorIndex,
                floorPoiCounts = floorPoiCounts,
                onFloorSelected = { floor -> viewModel.selectFloor(floor) },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
            )
        }

        // 4. Floating On-Screen Navigation Toolbar (Zoom In/Out, Fit, Labels, Mode)
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Zoom In [+]
            FloatingToolButton(
                icon = Icons.Default.Add,
                label = "IN",
                onClick = { viewModel.zoomIn() },
                testTag = "zoom_in_button"
            )

            // Live Zoom Percentage Display
            Box(
                modifier = Modifier
                    .size(width = 48.dp, height = 24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF141C26).copy(alpha = 0.95f))
                    .border(BorderStroke(1.dp, TarkovSurfaceBorder), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${(zoomScale * 100).toInt()}%",
                    color = TacticalCyan,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            // Zoom Out [-]
            FloatingToolButton(
                icon = Icons.Default.Remove,
                label = "OUT",
                onClick = { viewModel.zoomOut() },
                testTag = "zoom_out_button"
            )

            // Fit / Reset Camera [⟲]
            FloatingToolButton(
                icon = Icons.Default.CropFree,
                label = "FIT",
                onClick = { viewModel.resetCamera() },
                testTag = "fit_camera_button"
            )

            // Toggle Labels
            FloatingToolButton(
                icon = if (showLabels) Icons.Default.Label else Icons.Default.LabelOff,
                label = if (showLabels) "TAGS" else "OFF",
                onClick = { viewModel.toggleLabels() },
                tint = if (showLabels) TacticalAmber else TextMuted,
                testTag = "toggle_labels_button"
            )

            // 2D / 3D Toggle Quick Button
            FloatingToolButton(
                icon = Icons.Default.ViewInAr,
                label = if (is3DMode) "3D" else "2D",
                onClick = { viewModel.toggle3DMode() },
                tint = if (is3DMode) TacticalCyan else TacticalAmber,
                testTag = "toggle_3d_quick_button"
            )

            // Map Overlay Mode (SAT / HYBRID / TOPO)
            FloatingToolButton(
                icon = Icons.Default.Layers,
                label = overlayMode.shortLabel,
                onClick = { viewModel.toggleOverlayMode() },
                tint = when (overlayMode) {
                    MapOverlayMode.SATELLITE -> TacticalCyan
                    MapOverlayMode.TACTICAL_HYBRID -> TacticalAmber
                    MapOverlayMode.TOPO_SCHEMATIC -> Color(0xFF60A5FA)
                },
                testTag = "toggle_overlay_layer_button"
            )
        }

        // 5. Bottom Tactical Overlay: Compass, Grid Toggle, Map Intel Explorer Button
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 10.dp, bottom = if (selectedMarker != null) 140.dp else 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Compass
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF141C26).copy(alpha = 0.92f))
                    .border(BorderStroke(1.2.dp, TacticalAmber), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = "North",
                    tint = TacticalAmber,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(if (is3DMode) -25f else 0f)
                )
            }

            // Grid Toggle
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF141C26).copy(alpha = 0.92f))
                    .border(
                        BorderStroke(1.dp, if (showGrid) TacticalAmber else TarkovSurfaceBorder),
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { viewModel.toggleGrid() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("toggle_grid_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = "Grid",
                        tint = if (showGrid) TacticalAmber else TextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "GRID",
                        color = if (showGrid) TacticalAmber else TextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Map Intel POI Explorer Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(TacticalAmber.copy(alpha = 0.18f))
                    .border(BorderStroke(1.dp, TacticalAmber), RoundedCornerShape(6.dp))
                    .clickable { viewModel.openPoiDirectory() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("open_poi_directory_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "POIs",
                        tint = TacticalAmber,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "MAP INTEL (${selectedMap.lootSpawns.size + selectedMap.extractions.size})",
                        color = TacticalAmber,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Privacy & Security Disclosure Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF141C26).copy(alpha = 0.92f))
                    .border(BorderStroke(1.dp, TacticalCyan.copy(alpha = 0.6f)), RoundedCornerShape(6.dp))
                    .clickable { viewModel.openPrivacyPolicy() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("open_privacy_policy_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Privacy Policy",
                        tint = TacticalCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PRIVACY",
                        color = TacticalCyan,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 6. Docked POI Quick Inspector Card (When marker is clicked)
        selectedMarker?.let { marker ->
            DockedPoiCard(
                marker = marker,
                onOpenFullDetail = { viewModel.openFullDetail() },
                onCenterOnMarker = {
                    val (mx, my, mf) = when (marker) {
                        is TacticalMarker.Loot -> Triple(marker.spawn.x, marker.spawn.y, marker.spawn.floorIndex)
                        is TacticalMarker.Extract -> Triple(marker.extract.x, marker.extract.y, marker.extract.floorIndex)
                        is TacticalMarker.Boss -> Triple(marker.boss.x, marker.boss.y, marker.boss.floorIndex)
                        is TacticalMarker.Quest -> Triple(marker.quest.x, marker.quest.y, marker.quest.floorIndex)
                        is TacticalMarker.LockedDoor -> Triple(marker.lock.x, marker.lock.y, marker.lock.floorIndex)
                        is TacticalMarker.UserPin -> Triple(marker.pin.x, marker.pin.y, marker.pin.floorIndex)
                    }
                    viewModel.focusOnLocation(mx, my, mf, marker)
                },
                onDismiss = { viewModel.dismissMarkerDetail() },
                getLivePrice = { viewModel.getMarketItem(it) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            )
        }

        // 7. Full Tactical Detail Dialog (When "FULL INTEL" is tapped)
        if (showFullDetailDialog && selectedMarker != null) {
            TacticalDetailDialog(
                marker = selectedMarker!!,
                onDismiss = { viewModel.dismissFullDetail() },
                onDeletePin = { pinId -> viewModel.deletePin(pinId) }
            )
        }

        // 8. Custom Pin Placement Dialog
        dropPinCoords?.let { (x, y, floor) ->
            DropPinDialog(
                mapId = selectedMap.id,
                x = x,
                y = y,
                floorIndex = floor,
                onConfirm = { pin -> viewModel.saveCustomPin(pin) },
                onDismiss = { viewModel.dismissDropPin() }
            )
        }

        // 9. Extractions Checklist Dialog
        if (showExtractChecklist) {
            ExtractChecklistDialog(
                mapName = selectedMap.name,
                extracts = selectedMap.extractions,
                visitedExtractIds = visitedExtractIds,
                onToggleVisited = { viewModel.toggleExtractVisited(it) },
                onSelectExtract = { extract ->
                    viewModel.focusOnLocation(extract.x, extract.y, extract.floorIndex, TacticalMarker.Extract(extract))
                },
                onDismiss = { viewModel.dismissExtractChecklist() }
            )
        }

        // 10. Map Picker Sheet
        if (showMapPicker) {
            MapPickerSheet(
                maps = viewModel.maps,
                selectedMapId = selectedMap.id,
                onMapSelected = { map -> viewModel.selectMap(map) },
                onDismiss = { viewModel.dismissMapPicker() }
            )
        }

        // 11. POI Intel Directory Sheet
        if (showPoiDirectory) {
            PoiDirectorySheet(
                map = selectedMap,
                onSelectAndFocusMarker = { marker, x, y, floor ->
                    viewModel.focusOnLocation(x, y, floor, marker)
                },
                onDismiss = { viewModel.dismissPoiDirectory() }
            )
        }

        // 12. In-App Privacy & Security Policy Dialog
        if (showPrivacyPolicy) {
            PrivacyPolicyDialog(
                onDismiss = { viewModel.closePrivacyPolicy() }
            )
        }
    }
}

@Composable
private fun FloatingToolButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = TacticalAmber,
    testTag: String? = null
) {
    Box(
        modifier = modifier
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .size(width = 48.dp, height = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF141C26).copy(alpha = 0.92f))
            .border(BorderStroke(1.dp, TarkovSurfaceBorder), RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                color = tint,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
