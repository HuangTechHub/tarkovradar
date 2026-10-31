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
    is3DMode: Boolean,
    onToggle3DMode: () -> Unit,
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
