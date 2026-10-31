package com.example

import com.example.data.TarkovPresetsData
import com.example.model.ExtractFaction
import com.example.model.Rarity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TarkovRadarUnitTest {

    @Test
    fun testAll10MapsLoaded() {
        val maps = TarkovPresetsData.allMaps
        assertEquals(10, maps.size)

        val mapIds = maps.map { it.id }.toSet()
        assertTrue(mapIds.contains("customs"))
        assertTrue(mapIds.contains("reserve"))
        assertTrue(mapIds.contains("factory"))
        assertTrue(mapIds.contains("streets"))
        assertTrue(mapIds.contains("interchange"))
        assertTrue(mapIds.contains("the_lab"))
        assertTrue(mapIds.contains("woods"))
        assertTrue(mapIds.contains("shoreline"))
        assertTrue(mapIds.contains("lighthouse"))
        assertTrue(mapIds.contains("ground_zero"))
    }

    @Test
    fun testMultiFloorStructureData() {
        val customs = TarkovPresetsData.allMaps.first { it.id == "customs" }
        assertTrue("Customs should have multi-story levels", customs.floors.size >= 4)

        val reserve = TarkovPresetsData.allMaps.first { it.id == "reserve" }
        assertTrue("Reserve should feature subterranean bunker levels B2 and B1", reserve.floors.any { it.floorIndex < 0 })

        val lab = TarkovPresetsData.allMaps.first { it.id == "the_lab" }
        assertTrue("The Lab should feature sewage sub-floor B1", lab.floors.any { it.floorIndex == -1 })
    }

    @Test
    fun testLootRarityDistribution() {
        val customs = TarkovPresetsData.allMaps.first { it.id == "customs" }
        val sTierLoot = customs.lootSpawns.filter { it.rarity == Rarity.LEGENDARY }
        assertTrue("Customs should have legendary loot (e.g. Marked 314)", sTierLoot.isNotEmpty())
    }

    @Test
    fun testExtractionsConditions() {
        val customs = TarkovPresetsData.allMaps.first { it.id == "customs" }
        val carExtract = customs.extractions.firstOrNull { it.costRubles > 0 }
        assertNotNull("Customs should have SUV paid extraction", carExtract)
        assertEquals(5000, carExtract!!.costRubles)
    }

    @Test
    fun testAmmoBallisticsIntegrity() {
        val ammo = TarkovPresetsData.ammoList
        assertTrue("Ammunition matrix should have rich catalog", ammo.size >= 10)

        val m995 = ammo.first { it.id == "556_m995" }
        assertEquals(53, m995.penetration)
        assertEquals(6, m995.classEffectiveness.size) // Class 1 to 6
    }

    @Test
    fun testKeyDirectoryIntegrity() {
        val keys = TarkovPresetsData.keysList
        assertTrue("Keys directory should have high value keys", keys.size >= 5)
        val marked314 = keys.first { it.id == "k_314" }
        assertEquals("customs", marked314.mapId)
        assertTrue(marked314.estimatedValue > 1000000)
    }

    @Test
    fun testMarketPresetsIntegrity() {
        val items = com.example.data.market.TarkovMarketPresets.defaultItems
        assertTrue("Market catalog should contain high-value items", items.size >= 15)

        val redKeycard = items.first { it.id == "keycard_red" }
        assertTrue("Red Keycard should have substantial flea value", redKeycard.effectivePrice > 10000000)
        assertTrue("Red Keycard should have therapist trade price", redKeycard.bestTraderPriceRUB > 0)
        assertEquals(redKeycard.effectivePrice, redKeycard.pricePerSlot) // 1 slot item

        val gpu = items.first { it.id == "item_gpu" }
        assertEquals(2, gpu.slots)
        assertEquals(gpu.effectivePrice / 2, gpu.pricePerSlot)
    }

    @Test
    fun testMarketFuzzyLookup() {
        val repo = com.example.data.market.MarketRepository()
        val foundKey = repo.findItemByFuzzyName("314")
        assertNotNull("Should find marked 314 key by short search query", foundKey)
        assertTrue(foundKey!!.shortName.contains("314"))

        val foundLedx = repo.findItemByFuzzyName("ledx")
        assertNotNull("Should find LEDX by case-insensitive name", foundLedx)
        assertEquals("LEDX", foundLedx!!.shortName)
    }
}
