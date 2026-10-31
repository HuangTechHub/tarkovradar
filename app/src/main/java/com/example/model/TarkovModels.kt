package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.RarityCommon
import com.example.ui.theme.RarityEpic
import com.example.ui.theme.RarityLegendary
import com.example.ui.theme.RarityRare
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalBlue
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.TacticalHazardRed
import com.example.ui.theme.TacticalGreenBright

enum class Rarity(val label: String, val tier: String, val color: Color) {
    LEGENDARY("S-Tier Legendary", "S", RarityLegendary),
    EPIC("A-Tier High", "A", RarityEpic),
    RARE("B-Tier Medium", "B", RarityRare),
    COMMON("C-Tier Common", "C", RarityCommon)
}

enum class MapOverlayMode(val label: String, val shortLabel: String, val description: String) {
    SATELLITE("Satellite Recon", "SAT", "High-resolution aerial satellite reconnaissance overlay"),
    TACTICAL_HYBRID("Tactical Hybrid", "HYBRID", "Blended satellite terrain with high-contrast tactical schematics"),
    TOPO_SCHEMATIC("Topographic Vector", "TOPO", "Crisp military vector topographic blueprint")
}

enum class LootCategory(val displayName: String, val iconName: String) {
    VALUABLES("Valuables", "monetization_on"),
    TECH("Electronics & Tech", "memory"),
    MEDICAL("Medical Supplies", "medical_services"),
    KEYS_CARDS("Keys & Keycards", "vpn_key"),
    WEAPONS_AMMO("Weapons & Ammo", "shield"),
    PROVISIONS("Provisions", "restaurant"),
    CONTAINER("Containers & Safes", "inventory_2")
}

data class LootSpawn(
    val id: String,
    val name: String,
    val description: String,
    val rarity: Rarity,
    val category: LootCategory,
    val x: Float, // Normalized 0f..1000f map coordinate space
    val y: Float,
    val floorIndex: Int, // e.g. -2, -1, 0, 1, 2, 3
    val containerType: String, // e.g. "Safe", "PC Block", "Medcase", "Loose Spawn", "Wooden Crate"
    val estValueRoubles: Int,
    val dropChancePercent: Int = 100
)

enum class ExtractType(val label: String) {
    ALWAYS_OPEN("Guaranteed Open"),
    CONDITIONAL_SMOKE("Green Smoke / Flare Check"),
    CAR_PAID("Paid Vehicle Extract"),
    COOP_EXTRACT("PMC + Scav Friendly Co-Op"),
    RED_REBEL_PARACORD("Red Rebel + Paracord (No Armor)"),
    SWITCH_POWER("Requires Power / Bunker Switch")
}

enum class ExtractFaction(val label: String) {
    ALL("PMC & Scav"),
    PMC_ONLY("PMC Only"),
    SCAV_ONLY("Scav Only")
}

data class ExtractionPoint(
    val id: String,
    val name: String,
    val faction: ExtractFaction,
    val extractType: ExtractType,
    val requirements: String,
    val x: Float,
    val y: Float,
    val floorIndex: Int = 0,
    val costRubles: Int = 0,
    val hasGreenSmoke: Boolean = false,
    val hasFlares: Boolean = false
) {
    val factionColor: Color
        get() = when (faction) {
            ExtractFaction.PMC_ONLY -> TacticalEmerald
            ExtractFaction.SCAV_ONLY -> TacticalAmber
            ExtractFaction.ALL -> TacticalCyan
        }
}

data class BossSpawn(
    val name: String,
    val title: String,
    val spawnChancePercent: Int,
    val locationDescription: String,
    val guardCount: Int,
    val weapons: String,
    val tacticsHint: String,
    val x: Float,
    val y: Float,
    val floorIndex: Int = 0
)

data class QuestMarker(
    val id: String,
    val questName: String,
    val trader: String,
    val objectiveDescription: String,
    val requiredKey: String? = null,
    val x: Float,
    val y: Float,
    val floorIndex: Int = 0
)

data class FloorLevel(
    val id: String,
    val floorIndex: Int,
    val codeName: String,
    val displayName: String,
    val elevationOffsetZ: Float
)

data class MapStructure(
    val id: String,
    val name: String,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val floors: List<FloorLevel>,
    val description: String
)

enum class VectorFeatureType {
    BUILDING_OUTLINE,
    ROAD,
    WATER,
    RAILROAD,
    FENCE,
    STRUCTURE_INTERIOR,
    HAZARD_ZONE
}

data class MapVectorFeature(
    val id: String,
    val type: VectorFeatureType,
    val floorIndex: Int, // 99 for outdoor/all floors
    val points: List<Pair<Float, Float>>,
    val label: String? = null
)

data class LockedDoorMarker(
    val id: String,
    val keyName: String,
    val doorName: String,
    val mapLocation: String,
    val description: String,
    val x: Float,
    val y: Float,
    val floorIndex: Int = 0,
    val isMarkedRoom: Boolean = false,
    val estKeyPriceRubles: Int = 250000,
    val notableLoot: List<String> = emptyList(),
    val requiredForQuests: List<String> = emptyList()
)

data class TarkovMap(
    val id: String,
    val name: String,
    val code: String,
    val durationMinutes: Int,
    val playerCount: String,
    val difficulty: String,
    val description: String,
    val floors: List<FloorLevel>,
    val structures: List<MapStructure>,
    val lootSpawns: List<LootSpawn>,
    val extractions: List<ExtractionPoint>,
    val bosses: List<BossSpawn>,
    val quests: List<QuestMarker>,
    val vectorFeatures: List<MapVectorFeature>,
    val lockedDoors: List<LockedDoorMarker> = emptyList()
)

data class AmmoEntry(
    val id: String,
    val caliber: String,
    val name: String,
    val damage: Int,
    val penetration: Int,
    val armorDamagePct: Int,
    val fragChancePct: Int,
    val projectileVelocity: Int, // m/s
    // Effectiveness against armor classes 1 to 6 (6 is best, 0 is none)
    val classEffectiveness: List<Int>
)

data class KeyEntry(
    val id: String,
    val name: String,
    val mapId: String,
    val mapName: String,
    val lockLocation: String,
    val floorName: String,
    val estimatedValue: Int,
    val requiredQuests: List<String>,
    val notableLoot: List<String>
)
