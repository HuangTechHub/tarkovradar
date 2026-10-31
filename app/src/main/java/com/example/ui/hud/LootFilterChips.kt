package com.example.ui.hud

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.LootCategory
import com.example.model.Rarity
import com.example.ui.components.TacticalChip
import com.example.ui.theme.RarityCommon
import com.example.ui.theme.RarityEpic
import com.example.ui.theme.RarityLegendary
import com.example.ui.theme.RarityRare
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.TacticalHazardRed

@Composable
fun LootFilterChips(
    selectedRarities: Set<Rarity>,
    onToggleRarity: (Rarity) -> Unit,
    onResetRarities: () -> Unit,
    selectedCategory: LootCategory?,
    onSelectCategory: (LootCategory?) -> Unit,
    showExtracts: Boolean,
    onToggleExtracts: () -> Unit,
    showBosses: Boolean,
    onToggleBosses: () -> Unit,
    showLockedDoors: Boolean = true,
    onToggleLockedDoors: () -> Unit = {},
    showContainers: Boolean = true,
    onToggleContainers: () -> Unit = {},
    selectedContainerType: String? = null,
    onSelectContainerType: (String?) -> Unit = {},
    is3DMode: Boolean,
    onToggle3DMode: () -> Unit,
    overlayMode: com.example.model.MapOverlayMode = com.example.model.MapOverlayMode.SATELLITE,
    onToggleOverlayMode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("loot_filter_chips_bar"),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Map Overlay Layer Switcher (SAT / HYBRID / TOPO)
        TacticalChip(
            text = "LAYER: ${overlayMode.shortLabel}",
            isSelected = true,
            onClick = onToggleOverlayMode,
            activeColor = when (overlayMode) {
                com.example.model.MapOverlayMode.SATELLITE -> TacticalCyan
                com.example.model.MapOverlayMode.TACTICAL_HYBRID -> TacticalAmber
                com.example.model.MapOverlayMode.TOPO_SCHEMATIC -> androidx.compose.ui.graphics.Color(0xFF60A5FA)
            },
            testTag = "toggle_overlay_chip"
        )

        // 2D / 3D Isometric View Mode Switch
        TacticalChip(
            text = if (is3DMode) "MODE: 3D ISOMETRIC" else "MODE: 2D TOP-DOWN",
            isSelected = is3DMode,
            onClick = onToggle3DMode,
            activeColor = TacticalCyan,
            testTag = "toggle_3d_mode_chip"
        )

        // S-Tier Legendary quick toggle
        val isLegendaryActive = selectedRarities.contains(Rarity.LEGENDARY)
        TacticalChip(
            text = "★ S-TIER LEDX/GPU",
            isSelected = isLegendaryActive,
            onClick = { onToggleRarity(Rarity.LEGENDARY) },
            activeColor = RarityLegendary,
            testTag = "filter_rarity_legendary"
        )

        // A-Tier High
        val isEpicActive = selectedRarities.contains(Rarity.EPIC)
        TacticalChip(
            text = "A-TIER INTEL/KEYS",
            isSelected = isEpicActive,
            onClick = { onToggleRarity(Rarity.EPIC) },
            activeColor = RarityEpic,
            testTag = "filter_rarity_epic"
        )

        // B-Tier Meds
        val isRareActive = selectedRarities.contains(Rarity.RARE)
        TacticalChip(
            text = "B-TIER MEDS/AMMO",
            isSelected = isRareActive,
            onClick = { onToggleRarity(Rarity.RARE) },
            activeColor = RarityRare,
            testTag = "filter_rarity_rare"
        )

        // Extracts toggle
        TacticalChip(
            text = if (showExtracts) "EXFILS: ON" else "EXFILS: OFF",
            isSelected = showExtracts,
            onClick = onToggleExtracts,
            activeColor = TacticalEmerald,
            testTag = "filter_extracts_chip"
        )

        // Bosses toggle
        TacticalChip(
            text = if (showBosses) "BOSSES: ON" else "BOSSES: OFF",
            isSelected = showBosses,
            onClick = onToggleBosses,
            activeColor = TacticalHazardRed,
            testTag = "filter_bosses_chip"
        )

        // Keys & Locked Doors toggle (Tarkov.dev Parity)
        TacticalChip(
            text = if (showLockedDoors) "🔑 KEYS & LOCKS: ON" else "🔑 KEYS & LOCKS: OFF",
            isSelected = showLockedDoors,
            onClick = onToggleLockedDoors,
            activeColor = TacticalAmber,
            testTag = "filter_locked_doors_chip"
        )

        // Containers toggle (Tarkov.dev Parity)
        TacticalChip(
            text = if (showContainers) "📦 CONTAINERS: ON" else "📦 CONTAINERS: OFF",
            isSelected = showContainers,
            onClick = onToggleContainers,
            activeColor = TacticalCyan,
            testTag = "filter_containers_chip"
        )

        // Granular Container Types
        if (showContainers) {
            listOf("Safe", "PC Block", "Medcase", "Weapon Box", "Jacket", "Toolbox", "Dead Scav", "Duffle Bag").forEach { cType ->
                val isSelected = selectedContainerType?.equals(cType, ignoreCase = true) == true
                TacticalChip(
                    text = cType.uppercase(),
                    isSelected = isSelected,
                    onClick = {
                        onSelectContainerType(if (isSelected) null else cType)
                    },
                    activeColor = when (cType) {
                        "Safe" -> androidx.compose.ui.graphics.Color(0xFFF59E0B)
                        "PC Block" -> androidx.compose.ui.graphics.Color(0xFF38BDF8)
                        "Medcase" -> androidx.compose.ui.graphics.Color(0xFFEF4444)
                        "Weapon Box" -> androidx.compose.ui.graphics.Color(0xFF10B981)
                        "Jacket" -> androidx.compose.ui.graphics.Color(0xFFA855F7)
                        "Toolbox" -> androidx.compose.ui.graphics.Color(0xFFF97316)
                        else -> androidx.compose.ui.graphics.Color(0xFF94A3B8)
                    },
                    testTag = "filter_container_${cType.lowercase().replace(" ", "_")}"
                )
            }
        }

        // Category filter chips
        LootCategory.values().forEach { category ->
            val isCatSelected = selectedCategory == category
            TacticalChip(
                text = category.displayName.uppercase(),
                isSelected = isCatSelected,
                onClick = {
                    onSelectCategory(if (isCatSelected) null else category)
                },
                activeColor = TacticalAmber,
                testTag = "filter_category_${category.name}"
            )
        }
    }
}
