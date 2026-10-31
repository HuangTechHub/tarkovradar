package com.example.data

import com.example.model.AmmoEntry
import com.example.model.BossSpawn
import com.example.model.ExtractFaction
import com.example.model.ExtractType
import com.example.model.ExtractionPoint
import com.example.model.FloorLevel
import com.example.model.KeyEntry
import com.example.model.LootCategory
import com.example.model.LootSpawn
import com.example.model.LockedDoorMarker
import com.example.model.MapStructure
import com.example.model.MapVectorFeature
import com.example.model.QuestMarker
import com.example.model.Rarity
import com.example.model.TarkovMap
import com.example.model.VectorFeatureType

object TarkovPresetsData {

    val allMaps: List<TarkovMap> by lazy {
        listOf(
            createCustomsMap(),
            createReserveMap(),
            createFactoryMap(),
            createStreetsMap(),
            createInterchangeMap(),
            createTheLabMap(),
            createWoodsMap(),
            createShorelineMap(),
            createLighthouseMap(),
            createGroundZeroMap()
        )
    }

    private fun createCustomsMap(): TarkovMap {
        val floors = listOf(
            FloorLevel("customs_b1", -1, "B1", "Basement & Bunkers", -40f),
            FloorLevel("customs_g", 0, "G", "Ground & Yards", 0f),
            FloorLevel("customs_1f", 1, "1F", "1st Floor (Dorms/Offices)", 45f),
            FloorLevel("customs_2f", 2, "2F", "2nd Floor (Dorms 2&3 Story)", 90f),
            FloorLevel("customs_3f", 3, "3F", "3rd Floor (Marked Room 314)", 135f)
        )

        val structures = listOf(
            MapStructure("dorms_3", "3-Story Dorms (Marked 314)", 380f, 220f, 480f, 320f, floors, "Contains Room 314 Marked Room and Reshala boss spawn"),
            MapStructure("dorms_2", "2-Story Dorms", 490f, 240f, 570f, 320f, floors, "Safes, weapon boxes, Room 114 and 105"),
            MapStructure("fortress", "Stronghold / Fortress", 450f, 520f, 590f, 620f, floors, "Mounted grenade launcher, medical crates, Reshala & Goons spawn"),
            MapStructure("crackhouse", "Crackhouse (Med Building)", 340f, 530f, 420f, 610f, floors, "High-tier intel folders, surgical kits, and LEDX spawns"),
            MapStructure("big_red", "Customs Warehouse (Big Red)", 140f, 420f, 250f, 540f, floors, "Director's office safe and PC blocks"),
            MapStructure("new_gas", "New Gas Station", 650f, 480f, 740f, 560f, floors, "Medical supplies, weapon racks, Reshala spawn")
        )

        val lootSpawns = listOf(
            LootSpawn("c_loot_1", "Marked Room 314", "Keycard holder, Weapon cases, Bitcoins, Spear rifle", Rarity.LEGENDARY, LootCategory.VALUABLES, 430f, 240f, 3, "Marked Circle Loose", 1850000),
            LootSpawn("c_loot_2", "Crackhouse 2F Library", "Intelligence Folders, Flash Drives, Tech components", Rarity.EPIC, LootCategory.TECH, 380f, 550f, 2, "Bookshelf & Desk", 420000),
            LootSpawn("c_loot_3", "Crackhouse 1F Med Room", "LEDX spawn table, Ophthalmoscope, Salewa kits, Defibrillator", Rarity.LEGENDARY, LootCategory.MEDICAL, 360f, 570f, 1, "Medcase & Steel Table", 1200000),
            LootSpawn("c_loot_4", "Dorms 2-Story Safe 114", "Bitcoins, Rolex watches, Roubles, Euros", Rarity.EPIC, LootCategory.VALUABLES, 520f, 290f, 1, "Safe", 310000),
            LootSpawn("c_loot_5", "Stronghold 2F Intel Desk", "Military Flash Drive, AESA, Golden Star balm", Rarity.EPIC, LootCategory.TECH, 510f, 550f, 2, "Office Desk", 490000),
            LootSpawn("c_loot_6", "ZB-1012 Bunker Weapon Crate", "Modified AK-74M, High-Pen 5.45 BT/BS ammo boxes", Rarity.RARE, LootCategory.WEAPONS_AMMO, 780f, 640f, -1, "Weapon Box", 160000),
            LootSpawn("c_loot_7", "Big Red Director's Safe", "Tarcone office keys, Documents, Gold Chains", Rarity.RARE, LootCategory.VALUABLES, 180f, 460f, 2, "Safe", 220000),
            LootSpawn("c_loot_8", "Old Gas Station Table", "Random rare item spawn, Keycards, Moonshine", Rarity.EPIC, LootCategory.VALUABLES, 620f, 760f, 0, "Counter Table", 380000),
            LootSpawn("c_loot_9", "Dorms 204 Safe", "Valuable items, GP coins, Veritas guitar pick", Rarity.RARE, LootCategory.VALUABLES, 440f, 280f, 2, "Safe", 190000),
            LootSpawn("c_loot_10", "Dorms 214 Safe", "Physical Bitcoins, Lion statues, Cash", Rarity.EPIC, LootCategory.VALUABLES, 460f, 270f, 2, "Safe", 340000),
            LootSpawn("c_loot_11", "Customs River Stash", "Duffle bag with gear, class 4 armor plates", Rarity.COMMON, LootCategory.CONTAINER, 280f, 320f, 0, "Duffle Bag", 95000),
            LootSpawn("c_loot_12", "Skeletal Warehouse Medbox", "Morphine injectors, IFAK, CMS kit", Rarity.RARE, LootCategory.MEDICAL, 520f, 430f, 1, "Medcase", 140000),
            // Dedicated Containers
            LootSpawn("c_cont_pc1", "Big Red 2F PC Block", "Graphics Card, Flash Drive, CPU", Rarity.EPIC, LootCategory.CONTAINER, 175f, 450f, 2, "PC Block", 380000),
            LootSpawn("c_cont_jacket1", "Dorms 1F Hallway Jacket", "Marked Room 314 Key, Labs Keycard chance", Rarity.EPIC, LootCategory.CONTAINER, 420f, 280f, 1, "Jacket", 350000),
            LootSpawn("c_cont_tool1", "Fortress Technical Supply Crate", "Thermite, Corrugated hose, Car battery", Rarity.RARE, LootCategory.CONTAINER, 520f, 570f, 0, "Toolbox", 140000),
            LootSpawn("c_cont_dead1", "Boiler Railroad Dead Scav", "Casualty body with backpack, grenades", Rarity.COMMON, LootCategory.CONTAINER, 840f, 490f, 0, "Dead Scav", 85000)
        )

        val extractions = listOf(
            ExtractionPoint("c_ex_1", "ZB-1011 Bunker", ExtractFaction.PMC_ONLY, ExtractType.ALWAYS_OPEN, "Far east underground bunker. Always available for West spawns.", 890f, 780f, -1),
            ExtractionPoint("c_ex_2", "Crossroads", ExtractFaction.ALL, ExtractType.ALWAYS_OPEN, "West main gate near storage tanks. Guaranteed open.", 60f, 420f, 0),
            ExtractionPoint("c_ex_3", "Trailer Park", ExtractFaction.PMC_ONLY, ExtractType.ALWAYS_OPEN, "Southwest wooded extraction gate.", 70f, 680f, 0),
            ExtractionPoint("c_ex_4", "ZB-1012", ExtractFaction.PMC_ONLY, ExtractType.CONDITIONAL_SMOKE, "Open only if searchlight above bunker door is on.", 760f, 620f, -1, hasGreenSmoke = true),
            ExtractionPoint("c_ex_5", "Smuggler's Boat", ExtractFaction.PMC_ONLY, ExtractType.CONDITIONAL_SMOKE, "Active when campfire is burning by the riverbank.", 370f, 310f, 0, hasGreenSmoke = true),
            ExtractionPoint("c_ex_6", "Dorms V-Ex (SUV)", ExtractFaction.PMC_ONLY, ExtractType.CAR_PAID, "Requires 5,000 Roubles. 60-second departure timer. Up to 4 PMCs.", 470f, 160f, 0, costRubles = 5000),
            ExtractionPoint("c_ex_7", "Old Gas Station Gate", ExtractFaction.PMC_ONLY, ExtractType.CONDITIONAL_SMOKE, "Active if green smoke is lit behind the station gate.", 640f, 820f, 0, hasGreenSmoke = true),
            ExtractionPoint("c_ex_8", "RUAF Roadblock", ExtractFaction.ALL, ExtractType.CONDITIONAL_SMOKE, "Searchlight illuminated when available.", 320f, 680f, 0, hasGreenSmoke = true),
            ExtractionPoint("c_ex_9", "Admin Gate", ExtractFaction.SCAV_ONLY, ExtractType.ALWAYS_OPEN, "Industrial warehouse gate.", 860f, 540f, 0),
            ExtractionPoint("c_ex_10", "Factory Far Corner", ExtractFaction.SCAV_ONLY, ExtractType.ALWAYS_OPEN, "Southeastern concrete fence corner.", 910f, 910f, 0)
        )

        val bosses = listOf(
            BossSpawn(
                "Reshala",
                "Customs Mob Leader",
                35,
                "Dorms 2F/3F, New Gas Station, or Stronghold",
                4,
                "Golden TT-33 Pistol, AK-101, F-1 Grenades",
                "Guards use heavy armor and push aggressively with grenades. High chance of Golden TT.",
                440f, 270f, 2
            ),
            BossSpawn(
                "The Goons (Knight, Big Pipe, Birdeye)",
                "Rogue Boss Trio",
                15,
                "Stronghold / Fortress 2F and surrounding courtyard",
                0,
                "SCAR-H, Milkor M32A1 Grenade Launcher, RSASS Sniper",
                "Birdeye snipes silently while Big Pipe suppresses with heavy explosives and Knight pushes ruthlessly.",
                500f, 560f, 1
            )
        )

        val quests = listOf(
            QuestMarker("q1", "Checking", "Prapor", "Retrieve Bronze Pocket Watch from tanker truck near Construction", "Machinery Key", 410f, 480f, 0),
            QuestMarker("q2", "Operation Aquarius", "Therapist", "Find water stash in Room 206 of 2-Story Dorms", "Dorm Room 206 Key", 540f, 280f, 2),
            QuestMarker("q3", "Delivery from the Past", "Prapor", "Retrieve Tarcone Office documents in Big Red", "Customs Factory Key", 170f, 450f, 2)
        )

        val vectorFeatures = listOf(
            MapVectorFeature("v_road_main", VectorFeatureType.ROAD, 0, listOf(80f to 520f, 260f to 510f, 480f to 490f, 700f to 500f, 890f to 540f), "Main Highway"),
            MapVectorFeature("v_river", VectorFeatureType.WATER, 0, listOf(290f to 80f, 310f to 300f, 290f to 520f, 280f to 780f, 270f to 940f), "Customs River"),
            MapVectorFeature("v_rail", VectorFeatureType.RAILROAD, 0, listOf(260f to 880f, 540f to 830f, 750f to 760f, 900f to 740f), "Railway tracks"),
            MapVectorFeature("v_dorms_block", VectorFeatureType.BUILDING_OUTLINE, 99, listOf(370f to 200f, 590f to 200f, 590f to 340f, 370f to 340f, 370f to 200f), "Dorms Area"),
            MapVectorFeature("v_fortress_block", VectorFeatureType.BUILDING_OUTLINE, 99, listOf(440f to 510f, 600f to 510f, 600f to 630f, 440f to 630f, 440f to 510f), "Stronghold")
        )

        val lockedDoors = listOf(
            LockedDoorMarker("c_lock_314", "Dorm room 314 marked key", "Marked Room 314", "3-Story Dorms 3F", "Iconic marked room. Weapons, ammo cases, keycards, spears, bitcoins.", 430f, 240f, 3, isMarkedRoom = true, estKeyPriceRubles = 2850000, notableLoot = listOf("Keycard Holder", "Bitcoins", "Spear 6.8", "Weapon Cases"), requiredForQuests = listOf("The Cult - Part 2")),
            LockedDoorMarker("c_lock_114", "Dorms room 114 key", "Room 114 (2-Story)", "2-Story Dorms 1F", "Pharmacist quest room. Steel safe, PC block, and medcase.", 520f, 290f, 1, estKeyPriceRubles = 85000, notableLoot = listOf("Safe", "PC Block", "Medcase", "Pharmacist Case"), requiredForQuests = listOf("Pharmacist")),
            LockedDoorMarker("c_lock_214", "Dorms room 214 key", "Room 214 (3-Story)", "3-Story Dorms 2F", "Bedroom with safe, weapon boxes, rubles.", 460f, 270f, 2, estKeyPriceRubles = 65000, notableLoot = listOf("Safe", "Weapon Box", "Bitcoins")),
            LockedDoorMarker("c_lock_tarcone", "Tarcone Director's Office Key", "Director's Office", "Big Red Warehouse 2F", "Director office safe, 3x PC blocks, jackets, bronze pocket watch / documents.", 180f, 460f, 2, estKeyPriceRubles = 120000, notableLoot = listOf("Safe", "3x PC Blocks", "Tarcone Docs"), requiredForQuests = listOf("Delivery from the Past")),
            LockedDoorMarker("c_lock_usec", "USEC Stash Key", "USEC Stash Room", "Boiler Warehouse 1F", "Two heavy weapon boxes and technical supply crates.", 740f, 610f, 1, estKeyPriceRubles = 95000, notableLoot = listOf("2x Heavy Weapon Boxes", "Ammo Boxes")),
            LockedDoorMarker("c_lock_shortcut", "Factory Emergency Exit Key", "Customs Shortcut Gate", "Central Concrete Wall Gate", "Key opens the shortcut through the industrial fence separating East and West Customs.", 490f, 470f, 0, estKeyPriceRubles = 180000, notableLoot = listOf("Bypasses sniper danger and long trek"))
        )

        return TarkovMap(
            id = "customs",
            name = "Customs",
            code = "CUS",
            durationMinutes = 40,
            playerCount = "9-12 PMCs",
            difficulty = "Moderate (High PvP)",
            description = "Industrial landscape connecting the factory sector to the outskirts. Center of major early quests and high-intensity Dorms close-quarters combat.",
            floors = floors,
            structures = structures,
            lootSpawns = lootSpawns,
            extractions = extractions,
            bosses = bosses,
            quests = quests,
            vectorFeatures = vectorFeatures,
            lockedDoors = lockedDoors
        )
    }

    private fun createReserveMap(): TarkovMap {
        val floors = listOf(
            FloorLevel("res_b2", -2, "B2", "D-2 Underground Command & Bunkers", -80f),
            FloorLevel("res_b1", -1, "B1", "Sub-Basements & Connecting Tunnels", -40f),
            FloorLevel("res_g", 0, "G", "Surface Rail Yards & Courtyard", 0f),
            FloorLevel("res_1f", 1, "1F", "Barracks & Office Lower Floors", 45f),
            FloorLevel("res_2f", 2, "2F", "Barracks 2nd Floors", 90f),
            FloorLevel("res_3f", 3, "3F", "Queen Dome & Rooftops", 135f)
        )

        val structures = listOf(
            MapStructure("dome", "Radar Dome (Queen)", 320f, 180f, 440f, 300f, floors, "High ground sniper point, Cliff Descent extract, multiple safes"),
            MapStructure("bunker_d2", "D-2 Command Complex (Underground)", 380f, 380f, 620f, 540f, floors, "Massive sub-surface command bunker with power switch and D-2 extraction"),
            MapStructure("black_pawn", "Black Pawn Barracks", 320f, 420f, 420f, 520f, floors, "Marked room, military tech, underground tunnel entrance"),
            MapStructure("white_king", "White King (Control Center)", 480f, 360f, 580f, 460f, floors, "High tier tech spawn (AESA, VPX, Virtex), server racks"),
            MapStructure("train_station", "Depot & Train Station", 650f, 520f, 850f, 700f, floors, "Hermetic alarm extract, armored locomotive departure point")
        )

        val lootSpawns = listOf(
            LootSpawn("r_loot_1", "RB-PKPM Marked Room", "Cases, Keycards, High-tier firearms, Bitcoins", Rarity.LEGENDARY, LootCategory.VALUABLES, 350f, 450f, -1, "Marked Circle", 1950000),
            LootSpawn("r_loot_2", "D-2 Server Command Tech", "Military COFDM, Virtex, Iridium, AESA battery", Rarity.LEGENDARY, LootCategory.TECH, 490f, 470f, -2, "Server Rack", 1450000),
            LootSpawn("r_loot_3", "Dome Guard Office Safes", "RB-KPRL dual safes, intel folders, SAS drives", Rarity.EPIC, LootCategory.VALUABLES, 360f, 220f, 1, "Dual Steel Safes", 550000),
            LootSpawn("r_loot_4", "White King Server Room", "Phased Array Element (AESA), Graphics Cards", Rarity.LEGENDARY, LootCategory.TECH, 520f, 410f, 1, "Computer Tower Table", 1300000),
            LootSpawn("r_loot_5", "Black Bishop 2F Med Room", "Ophthalmoscope, LEDX, Morphine, Grizzly", Rarity.EPIC, LootCategory.MEDICAL, 430f, 380f, 2, "Pharmacy Cabinets", 680000),
            LootSpawn("r_loot_6", "Armored Train Platform Crates", "Military cables, tank battery, heavy ordnance", Rarity.RARE, LootCategory.WEAPONS_AMMO, 740f, 610f, 0, "Heavy Technical Crate", 290000),
            LootSpawn("r_loot_7", "RB-ST Armory", "OFZ 30mm shells, AESA, multiple high-pen ammo packs", Rarity.EPIC, LootCategory.WEAPONS_AMMO, 710f, 440f, 0, "Secured Armory Cage", 720000)
        )

        val extractions = listOf(
            ExtractionPoint("r_ex_1", "D-2 Bunker Exit", ExtractFaction.PMC_ONLY, ExtractType.SWITCH_POWER, "Must activate bunker power switch in sub-level first. Press door button to open.", 540f, 520f, -2),
            ExtractionPoint("r_ex_2", "Cliff Descent (Dome)", ExtractFaction.PMC_ONLY, ExtractType.RED_REBEL_PARACORD, "Requires Red Rebel Ice Pick + Paracord in inventory. Cannot wear armored body rig.", 310f, 160f, 3),
            ExtractionPoint("r_ex_3", "Bunker Hermetic Door", ExtractFaction.ALL, ExtractType.SWITCH_POWER, "Alarm lever must be activated near White Pawn. 4-minute siren window.", 820f, 480f, 0, hasFlares = true),
            ExtractionPoint("r_ex_4", "Armored Train", ExtractFaction.ALL, ExtractType.ALWAYS_OPEN, "Train arrives between 25-35 minutes into raid. Sounds horn twice before departure.", 760f, 650f, 0),
            ExtractionPoint("r_ex_5", "Sewer Manhole", ExtractFaction.ALL, ExtractType.ALWAYS_OPEN, "Quick exit. Backpack CANNOT be equipped.", 540f, 320f, 0),
            ExtractionPoint("r_ex_6", "Scav Lands (Co-Op)", ExtractFaction.ALL, ExtractType.COOP_EXTRACT, "Requires friendly PMC and Scav extracting together. Fence gift rewarded.", 620f, 210f, 0),
            ExtractionPoint("r_ex_7", "Heating Pipe", ExtractFaction.SCAV_ONLY, ExtractType.ALWAYS_OPEN, "Industrial heating pipes on perimeter.", 880f, 360f, 0)
        )

        val bosses = listOf(
            BossSpawn(
                "Glukhar",
                "Reserve Military Commander",
                30,
                "Train Station, Black/White Pawn, or K-Buildings",
                6,
                "ASh-12 12.7mm, M1A EBR, Class 6 Zabralo Armor",
                "Spawns with 6 elite guards (assault, security, and scouts). Extremely lethal in close quarters.",
                520f, 440f, 0
            )
        )

        val quests = listOf(
            QuestMarker("res_q1", "The Bunker - Part 1", "Prapor", "Locate the underground control bunker room", null, 480f, 460f, -2),
            QuestMarker("res_q2", "Disease History", "Therapist", "Find Medical Records in White & Black Bishop", "RB-KSM & RB-SMP keys", 420f, 390f, 2)
        )

        val vectorFeatures = listOf(
            MapVectorFeature("res_v_dome_b", VectorFeatureType.BUILDING_OUTLINE, 99, listOf(300f to 160f, 460f to 160f, 460f to 320f, 300f to 320f, 300f to 160f), "Dome Complex"),
            MapVectorFeature("res_v_bunker_tunnel", VectorFeatureType.STRUCTURE_INTERIOR, -1, listOf(360f to 240f, 400f to 360f, 500f to 450f, 600f to 520f), "Underground Tunnel System"),
            MapVectorFeature("res_v_rail_yard", VectorFeatureType.RAILROAD, 0, listOf(620f to 800f, 680f to 600f, 750f to 450f, 850f to 300f), "Train Tracks")
        )

        val lockedDoors = listOf(
            LockedDoorMarker("res_lock_rbbk", "RB-BK Marked Key", "Black Pawn Marked Room", "Black Pawn Basement B1", "Underground marked circle: weapon cases, keycards, spears, bitcoins.", 420f, 480f, -1, isMarkedRoom = true, estKeyPriceRubles = 1850000, notableLoot = listOf("Keycards", "Weapon Cases", "Bitcoins")),
            LockedDoorMarker("res_lock_rbvo", "RB-VO Marked Key", "Barracks Marked Room", "White Bishop 1F", "Marked room circle with high tier weapons, injectors, ammo.", 580f, 450f, 0, isMarkedRoom = true, estKeyPriceRubles = 1450000, notableLoot = listOf("Weapons", "Injectors", "Bitcoins")),
            LockedDoorMarker("res_lock_rbst", "RB-ST Key", "Military Garage Stash", "Vehicle Repair Hangar G", "High tier military loot: OFZ shells, Military Tank Battery, heavy weapon boxes.", 350f, 380f, 0, estKeyPriceRubles = 950000, notableLoot = listOf("Tank Battery", "OFZ Shells", "Weapon Boxes"), requiredForQuests = listOf("The Bunker")),
            LockedDoorMarker("res_lock_rbak", "RB-AK Key", "Black Bishop 2F Tech Room", "Black Bishop 2F", "Multiple Graphics Cards (GPU) spawns, 3x PC blocks, Tetriz.", 440f, 420f, 2, estKeyPriceRubles = 620000, notableLoot = listOf("Graphics Cards", "Tetriz", "PC Blocks")),
            LockedDoorMarker("res_lock_rbrltb", "RB-RLTB Key", "D-2 Radar Station Room", "Dome / Bunker Level B2", "Military intelligence folders, classified plans, weapon safe.", 510f, 230f, 3, estKeyPriceRubles = 450000, notableLoot = listOf("Intelligence Folders", "Safe", "Military Cables"))
        )

        return TarkovMap(
            id = "reserve",
            name = "Reserve (Military Base)",
            code = "RES",
            durationMinutes = 40,
            playerCount = "9-12 PMCs",
            difficulty = "Hard",
            description = "State Reserve base codenamed 'Reserve'. Massive subterranean bunker system (D-2), radar dome high ground, and intense raider combat.",
            floors = floors,
            structures = structures,
            lootSpawns = lootSpawns,
            extractions = extractions,
            bosses = bosses,
            quests = quests,
            vectorFeatures = vectorFeatures,
            lockedDoors = lockedDoors
        )
    }

    private fun createFactoryMap(): TarkovMap {
        val floors = listOf(
            FloorLevel("fac_b1", -1, "B1", "Cellars & Underground Tunnels", -35f),
            FloorLevel("fac_g", 0, "G", "Main Factory Floor", 0f),
            FloorLevel("fac_1f", 1, "1F", "Offices 2nd Floor & Lockers", 40f),
            FloorLevel("fac_2f", 2, "2F", "Offices 3rd Floor & Safe Room", 80f),
            FloorLevel("fac_3f", 3, "3F", "Overhead Crane & Catwalks", 120f)
        )

        val structures = listOf(
            MapStructure("office_bldg", "3-Story Office Complex", 400f, 310f, 600f, 490f, floors.filter { it.floorIndex in 0..2 }, "Offices: Safe Room (2F), Breach Room & Lockers (1F), Breakroom (G)"),
            MapStructure("catwalk_system", "Overhead Catwalk Bridges & Crane", 220f, 280f, 780f, 520f, floors.filter { it.floorIndex == 3 }, "Rafters & catwalk walkways spanning above the factory floor"),
            MapStructure("silos_tanks", "Chemical Silos & Gas Tanks", 200f, 220f, 350f, 440f, floors.filter { it.floorIndex == 0 }, "Massive industrial chemical storage cylinders"),
            MapStructure("pumping_station", "Pumping Station & Pipelines", 210f, 560f, 350f, 720f, floors.filter { it.floorIndex in -1..0 }, "Pumping station and conduit manifolds"),
            MapStructure("forklift_area", "Forklift & Shipping Containers", 650f, 220f, 800f, 450f, floors.filter { it.floorIndex == 0 }, "Forklift staging arena and blue shipping containers"),
            MapStructure("underground_cellars", "Underground Cellars & Tunnel Network", 250f, 250f, 750f, 750f, floors.filter { it.floorIndex == -1 }, "Complete subterranean drainage and access network")
        )

        val lootSpawns = listOf(
            LootSpawn("f_loot_1", "3F Office Safe", "Bitcoins, Prokill medallions, Flash drives, Cash", Rarity.EPIC, LootCategory.VALUABLES, 510f, 360f, 2, "Office Safe", 420000),
            LootSpawn("f_loot_2", "Locked Breach Room 2F", "Loose weapon mods, rare meds, suppressors", Rarity.RARE, LootCategory.WEAPONS_AMMO, 490f, 400f, 1, "Wooden Floor Loose", 160000),
            LootSpawn("f_loot_3", "Underground Medical Crate", "CMS kits, Morphine, Salewa packs", Rarity.RARE, LootCategory.MEDICAL, 380f, 560f, -1, "Medical Box", 150000),
            LootSpawn("f_loot_4", "Pumping Station Manifold", "Gas analyzers, water filters, rare tools", Rarity.COMMON, LootCategory.TECH, 280f, 640f, 0, "Metal Cabinets", 95000),
            LootSpawn("f_loot_5", "Catwalk Crane Sniper Stash", "Sniper Rifle SV-98, SNB high-pen ammo, Scope", Rarity.EPIC, LootCategory.WEAPONS_AMMO, 500f, 300f, 3, "Catwalk Metal Crate", 320000),
            LootSpawn("f_loot_6", "Silos Dead Scav Stash", "Duffle bag with injectors, weapon parts", Rarity.COMMON, LootCategory.CONTAINER, 260f, 340f, 0, "Dead Scav Body", 85000),
            LootSpawn("f_loot_7", "Forklift Weapon Box", "AK-74U, 5.45 BT ammo, grenades", Rarity.RARE, LootCategory.WEAPONS_AMMO, 720f, 320f, 0, "Wooden Weapon Box", 140000),
            LootSpawn("f_loot_8", "Cellars Pipe Tool Box", "Military corrugated hose, spark plugs, wires", Rarity.COMMON, LootCategory.TECH, 560f, 620f, -1, "Technical Supply Crate", 110000)
        )

        val extractions = listOf(
            ExtractionPoint("f_ex_1", "Gate 3", ExtractFaction.ALL, ExtractType.ALWAYS_OPEN, "Southeastern corner double metal doors. Guaranteed open.", 850f, 820f, 0),
            ExtractionPoint("f_ex_2", "Gate 0", ExtractFaction.ALL, ExtractType.ALWAYS_OPEN, "Requires Factory Emergency Exit Key. Northwest corner.", 170f, 200f, 0),
            ExtractionPoint("f_ex_3", "Cellars", ExtractFaction.PMC_ONLY, ExtractType.ALWAYS_OPEN, "Requires Factory Emergency Exit Key. Located in sub-floor tunnels.", 580f, 680f, -1),
            ExtractionPoint("f_ex_4", "Camera Bunker Door", ExtractFaction.SCAV_ONLY, ExtractType.ALWAYS_OPEN, "Sub-tunnel red light door.", 380f, 740f, -1),
            ExtractionPoint("f_ex_5", "Office Window", ExtractFaction.SCAV_ONLY, ExtractType.ALWAYS_OPEN, "Ground floor shattered window.", 510f, 470f, 0)
        )

        val bosses = listOf(
            BossSpawn(
                "Tagilla",
                "Factory Sledgehammer Boss",
                30,
                "Patrols throughout tunnels, silos, and 1F container area",
                0,
                "Massive Sledgehammer, Saiga-12, Welding Mask (Class 5/6)",
                "Charges frantically with sledgehammer when close; switches to heavy shotgun at mid-range. Aim for unarmored legs.",
                480f, 520f, 0
            )
        )

        val quests = listOf(
            QuestMarker("f_q1", "Delivery from the Past", "Prapor", "Plant the customs documents in Night Factory stage 2", null, 620f, 420f, 0),
            QuestMarker("f_q2", "Postman Pat - Part 1", "Prapor", "Search dead letter carrier on 1F near bunkers", null, 380f, 620f, 0)
        )

        val vectorFeatures = listOf(
            MapVectorFeature("f_v_walls", VectorFeatureType.BUILDING_OUTLINE, 99, listOf(140f to 140f, 860f to 140f, 860f to 860f, 140f to 860f, 140f to 140f), "Factory Exterior Perimeter"),
            MapVectorFeature("f_v_office", VectorFeatureType.STRUCTURE_INTERIOR, 99, listOf(400f to 310f, 600f to 310f, 600f to 490f, 400f to 490f, 400f to 310f), "Offices Outline"),
            MapVectorFeature("f_v_pipes", VectorFeatureType.ROAD, 0, listOf(260f to 220f, 260f to 500f, 500f to 500f, 740f to 500f), "Main Pipeline"),
            MapVectorFeature("f_v_rail", VectorFeatureType.RAILROAD, 0, listOf(300f to 860f, 500f to 700f, 500f to 300f), "Material Transport Tracks")
        )

        val lockedDoors = listOf(
            LockedDoorMarker("fac_lock_exit", "Factory Emergency Exit Key", "Factory Gate 0 & Cellars", "Factory Perimeter G/-1F", "Opens Gate 0 extraction and Cellars extraction, avoiding the deadly Gate 3 camper trap.", 210f, 340f, 0, estKeyPriceRubles = 180000, notableLoot = listOf("Emergency Exfil Access", "Cellars Bypass")),
            LockedDoorMarker("fac_lock_pumping", "Pumping Station Key", "Locked Pumping Station", "Factory Central Hall 1F", "Pumping station storage room with Gas Analyzers and quest documents.", 520f, 480f, 1, estKeyPriceRubles = 45000, notableLoot = listOf("Gas Analyzers", "Bleach"), requiredForQuests = listOf("Bad Rep Evidence"))
        )

        return TarkovMap(
            id = "factory",
            name = "Factory",
            code = "FAC",
            durationMinutes = 20,
            playerCount = "4-6 PMCs",
            difficulty = "Very High (Instant PvP)",
            description = "Compact industrial chemical plant. Lightning fast skirmishes, close-quarters combat, and high-adrenaline Tagilla hammer encounters.",
            floors = floors,
            structures = structures,
            lootSpawns = lootSpawns,
            extractions = extractions,
            bosses = bosses,
            quests = quests,
            vectorFeatures = vectorFeatures,
            lockedDoors = lockedDoors
        )
    }

    private fun createStreetsMap(): TarkovMap {
        val floors = listOf(
            FloorLevel("str_b1", -1, "B1", "Subway & Concordia Underground", -40f),
            FloorLevel("str_g", 0, "G", "Boulevards, LexOs & Courtyards", 0f),
            FloorLevel("str_1f", 1, "1F", "1st Floor Stores & Hotel Rooms", 45f),
            FloorLevel("str_2f", 2, "2F", "2nd Floor Luxury Suites & Offices", 90f),
            FloorLevel("str_3f", 3, "3F", "Pinewood Rooftops & Penthouses", 135f)
        )

        val structures = listOf(
            MapStructure("lexos", "LexOs Car Dealership (Kaban)", 450f, 480f, 620f, 620f, floors, "Mined perimeter, heavy machine gun nests, Kaban boss lair"),
            MapStructure("concordia", "Concordia Residential Complex", 220f, 600f, 380f, 750f, floors, "Apartments with high-tier tech, parking garage, marked room"),
            MapStructure("pinewood", "Pinewood Hotel", 350f, 220f, 520f, 380f, floors, "Multi-level hotel with medical rooms, dental clinic, and safes"),
            MapStructure("post_office", "Post Office & Bank", 620f, 280f, 740f, 420f, floors, "Financial records, safety deposit boxes, tech loot")
        )

        val lootSpawns = listOf(
            LootSpawn("str_loot_1", "Concordia Marked Room 64", "Keycards, Spear rifle, Bitcoins, AESA", Rarity.LEGENDARY, LootCategory.VALUABLES, 310f, 680f, 2, "Marked Floor", 2100000),
            LootSpawn("str_loot_2", "LexOs Manager Safe Room", "Bitcoins, Moonshine, High-tier Western gear", Rarity.LEGENDARY, LootCategory.VALUABLES, 540f, 530f, 1, "Safe", 1350000),
            LootSpawn("str_loot_3", "Pinewood Dental Office", "LEDX, Ophthalmoscope, Golden Star, Stims", Rarity.EPIC, LootCategory.MEDICAL, 440f, 310f, 1, "Medcase", 850000),
            LootSpawn("str_loot_4", "Finance Center Server Racks", "Graphics Cards (GPU), Virtex, Microcontrollers", Rarity.LEGENDARY, LootCategory.TECH, 680f, 340f, 1, "PC Block", 1600000),
            LootSpawn("str_loot_5", "Chekannaya 15 Marked Apartment", "Top tier weapons, Keycards, Moonshine cases", Rarity.LEGENDARY, LootCategory.VALUABLES, 640f, 510f, 2, "Marked Floor", 2400000),
            LootSpawn("str_cont_safe1", "Concordia Safe 1", "Bitcoins, Rolers, Euro cash", Rarity.EPIC, LootCategory.CONTAINER, 300f, 670f, 2, "Safe", 320000),
            LootSpawn("str_cont_pc1", "Post Office PC Block", "Graphics Cards, Flash drives, CPU", Rarity.EPIC, LootCategory.CONTAINER, 660f, 330f, 1, "PC Block", 390000),
            LootSpawn("str_cont_jacket1", "Pinewood 2F Hallway Jacket", "Chek 15 Key, Concordia Keys", Rarity.EPIC, LootCategory.CONTAINER, 430f, 280f, 2, "Jacket", 290000)
        )

        val extractions = listOf(
            ExtractionPoint("str_ex_1", "Courtyard", ExtractFaction.ALL, ExtractType.CONDITIONAL_SMOKE, "Active when green flare is burning in the inner courtyard.", 490f, 370f, 0, hasGreenSmoke = true),
            ExtractionPoint("str_ex_2", "Sewer Manhole", ExtractFaction.ALL, ExtractType.ALWAYS_OPEN, "Requires dropping backpack.", 320f, 520f, 0),
            ExtractionPoint("str_ex_3", "Underpass (Primorsky)", ExtractFaction.PMC_ONLY, ExtractType.ALWAYS_OPEN, "Underground pedestrian crossing.", 180f, 420f, -1),
            ExtractionPoint("str_ex_4", "Klimov Street (Green Flare)", ExtractFaction.ALL, ExtractType.CONDITIONAL_SMOKE, "MUST fire green signal flare towards sniper zone before walking across.", 720f, 180f, 0, hasFlares = true),
            ExtractionPoint("str_ex_5", "Pinewood Basement", ExtractFaction.SCAV_ONLY, ExtractType.ALWAYS_OPEN, "Hotel basement storage corridor.", 430f, 260f, -1)
        )

        val bosses = listOf(
            BossSpawn(
                "Kaban & Kollontay",
                "City Syndicate Bosses",
                25,
                "LexOs Dealership / Klimov Shopping Center",
                8,
                "PKP Pecheneg 7.62x54R, RPD Machine Gun, RGo Grenades",
                "Surrounded by heavy sniper snipers, stationary AGS-30, and heavily plated bodyguards.",
                520f, 550f, 0
            )
        )

        val quests = listOf(
            QuestMarker("str_q1", "Audiophile", "Ragman", "Retrieve master tape from Concordia music studio", "Concordia key", 280f, 650f, 2),
            QuestMarker("str_q2", "Ballet Lover", "Therapist", "Investigate prima ballerina apartment in Primorsky", null, 420f, 240f, 1)
        )

        val vectorFeatures = listOf(
            MapVectorFeature("str_v_boulevard", VectorFeatureType.ROAD, 0, listOf(100f to 500f, 350f to 500f, 650f to 500f, 900f to 500f), "Primorsky Prospekt"),
            MapVectorFeature("str_v_lexos_b", VectorFeatureType.BUILDING_OUTLINE, 99, listOf(440f to 470f, 630f to 470f, 630f to 630f, 440f to 630f, 440f to 470f), "LexOs Dealership Compound")
        )

        val lockedDoors = listOf(
            LockedDoorMarker("str_lock_chek15", "Chekannaya 15 Apartment Key", "Chek 15 Marked Apartment", "Chekannaya Street 2F", "Number one marked room on Streets. LEDX, Bitcoins, Spear, Moonshine, Keycards.", 480f, 380f, 2, isMarkedRoom = true, estKeyPriceRubles = 3400000, notableLoot = listOf("Bitcoins", "LEDX", "Spear 6.8", "Keycards"), requiredForQuests = listOf("You've Got Mail")),
            LockedDoorMarker("str_lock_concordia64", "Concordia Apartment 64 Key", "Concordia 64 Apartment", "Concordia Complex 2F", "Luxury residential apartment with 2x safes, bitcoins, and rare electronic items.", 290f, 640f, 2, estKeyPriceRubles = 890000, notableLoot = listOf("2x Safes", "Bitcoins", "Graphics Cards")),
            LockedDoorMarker("str_lock_pinewood215", "Pinewood Room 215 Key", "Pinewood Hotel Room 215", "Pinewood Hotel 2F", "Hotel suite safe, PC block, and weapon crate.", 670f, 310f, 2, estKeyPriceRubles = 240000, notableLoot = listOf("Safe", "PC Block", "Weapon Box")),
            LockedDoorMarker("str_lock_kaban", "Kaban Dealership Office Key", "LexOs Dealership Office", "LexOs Dealership 1F", "Kaban's private executive office. Modded PKM machine gun ammo, safes.", 550f, 580f, 1, estKeyPriceRubles = 780000, notableLoot = listOf("PKM Ammo", "Safes", "Armor Plates"))
        )

        return TarkovMap(
            id = "streets",
            name = "Streets of Tarkov",
            code = "STR",
            durationMinutes = 50,
            playerCount = "15-18 PMCs",
            difficulty = "Expert",
            description = "Sprawling metropolitan downtown center of Tarkov. High density of multi-story buildings, luxury car dealership, and extreme loot density.",
            floors = floors,
            structures = structures,
            lootSpawns = lootSpawns,
            extractions = extractions,
            bosses = bosses,
            quests = quests,
            vectorFeatures = vectorFeatures,
            lockedDoors = lockedDoors
        )
    }

    private fun createInterchangeMap(): TarkovMap {
        val floors = listOf(
            FloorLevel("int_b1", -1, "B1", "Underground Parking (IDEA, OLI, Goshan)", -50f),
            FloorLevel("int_1f", 1, "1F", "1st Floor Mall (Kiba, Mantis, Goshan)", 0f),
            FloorLevel("int_2f", 2, "2F", "2nd Floor (Techlight, Ultra Med, Food Court)", 55f)
        )

        val structures = listOf(
            MapStructure("ultra_mall", "ULTRA Shopping Mall", 250f, 250f, 750f, 750f, floors, "Gigantic mega-mall housing major electronics, medical stores, and Kiba Arms"),
            MapStructure("kiba_arms", "KIBA Arms High-End Armory", 480f, 480f, 540f, 540f, floors, "Class 5/6 thermal scopes, modded M4A1, weapon wall"),
            MapStructure("techlight", "Techlight & Ultra Medical", 620f, 450f, 700f, 530f, floors, "Primary LEDX and GPU spawn location")
        )

        val lootSpawns = listOf(
            LootSpawn("int_loot_1", "Techlight Electronics", "Graphics Cards (GPU), Tetriz, Power Supplies", Rarity.LEGENDARY, LootCategory.TECH, 650f, 480f, 2, "Display Shelves", 1500000),
            LootSpawn("int_loot_2", "Ultra Medical Room", "LEDX, Ophthalmoscope, Defibrillator, Stims", Rarity.LEGENDARY, LootCategory.MEDICAL, 640f, 510f, 2, "Pharma Shelves (Key Required)", 1800000),
            LootSpawn("int_loot_3", "KIBA Arms Weapon Racks", "Thermal scopes, REAP-IR, SOPMOD M4, AS VAL", Rarity.EPIC, LootCategory.WEAPONS_AMMO, 510f, 500f, 1, "Secured Armory Wall", 950000),
            LootSpawn("int_loot_4", "Rasmussen Tech Counter", "Tetriz, GPUs, military cables, relay switch", Rarity.EPIC, LootCategory.TECH, 420f, 520f, 1, "Counter Shelving", 720000),
            LootSpawn("int_loot_5", "Goshan Food Shelves", "Tushonka, Condensed Milk, TarCola, Iskra", Rarity.COMMON, LootCategory.PROVISIONS, 500f, 680f, 1, "Grocery Aisle", 85000)
        )

        val extractions = listOf(
            ExtractionPoint("int_ex_1", "Railway Exfil", ExtractFaction.ALL, ExtractType.ALWAYS_OPEN, "Northwestern train tracks along the perimeter fence.", 120f, 140f, 1),
            ExtractionPoint("int_ex_2", "Emercom Medical Checkpoint", ExtractFaction.ALL, ExtractType.ALWAYS_OPEN, "Southeastern ambulances along highway.", 880f, 880f, 1),
            ExtractionPoint("int_ex_3", "Hole in Fence", ExtractFaction.ALL, ExtractType.ALWAYS_OPEN, "Backpack CANNOT be equipped.", 160f, 740f, 1),
            ExtractionPoint("int_ex_4", "Power Station V-Ex", ExtractFaction.PMC_ONLY, ExtractType.CAR_PAID, "Requires 5,000 Roubles. Leaves after 60s.", 860f, 220f, 1, costRubles = 5000),
            ExtractionPoint("int_ex_5", "Saferoom Exfil", ExtractFaction.PMC_ONLY, ExtractType.SWITCH_POWER, "Must activate power and toilet keycard swipe #11SR in burger shop.", 510f, 540f, -1)
        )

        val bosses = listOf(
            BossSpawn(
                "Killa",
                "The Mall Executioner",
                30,
                "Patrols 1F/2F center corridors, Kiba, Mantis, or Goshan",
                0,
                "RPK-16 with 95-round drum, Igolnik ammo, Maska-1Sch Helmet",
                "Solo aggressive juggernaut. Slides around corners, pre-fires with devastating 5.45x39 ammo, and rushes healing players.",
                500f, 500f, 1
            )
        )

        val quests = listOf(
            QuestMarker("int_q1", "The Blood of War - Part 1", "Ragman", "Mark fuel tanks in OLI, IDEA and Goshan", "MS2000 Marker", 520f, 650f, 1),
            QuestMarker("int_q2", "Big Sale", "Ragman", "Check 5 major department stores inside the mall", null, 480f, 480f, 1)
        )

        val vectorFeatures = listOf(
            MapVectorFeature("int_v_mall_outline", VectorFeatureType.BUILDING_OUTLINE, 99, listOf(240f to 240f, 760f to 240f, 760f to 760f, 240f to 760f, 240f to 240f), "ULTRA Mall Complex"),
            MapVectorFeature("int_v_highway", VectorFeatureType.ROAD, 1, listOf(80f to 880f, 500f to 880f, 920f to 880f), "Main Access Highway")
        )

        val lockedDoors = listOf(
            LockedDoorMarker("int_lock_kiba_out", "KIBA Store Outer Door Key", "KIBA Arms Outer Gate", "ULTRA Mall Center 1F", "Requires mall power on. First barrier protecting KIBA armory.", 500f, 490f, 1, estKeyPriceRubles = 750000, notableLoot = listOf("Thermal Scopes", "Modded M4A1", "Armor Plates"), requiredForQuests = listOf("Gunsmith - Part 11")),
            LockedDoorMarker("int_lock_kiba_in", "KIBA Store Inner Door Key", "KIBA Arms Inner Grate", "ULTRA Mall Center 1F", "Inside KIBA: RSASS, SOPMOD M4, suppressors, armor plate carriers.", 510f, 495f, 1, estKeyPriceRubles = 680000, notableLoot = listOf("RSASS Sniper", "SOPMOD M4", "Tier 5/6 Armor")),
            LockedDoorMarker("int_lock_ultra", "ULTRA Medical Storage Key", "ULTRA Medical Room", "ULTRA Mall 2F above Techlight", "Requires mall power on. Known for multiple LEDX spawns and Defibrillators.", 540f, 380f, 2, estKeyPriceRubles = 2100000, notableLoot = listOf("LEDX Spawns", "Defibrillators", "Ophthalmoscopes")),
            LockedDoorMarker("int_lock_emercom", "EMERCOM Medical Unit Key", "EMERCOM Medical Unit", "ULTRA Mall 1F North", "Large triage room with 3x Medbags, medical crates, LEDX chance.", 420f, 360f, 1, estKeyPriceRubles = 340000, notableLoot = listOf("3x Medcases", "LEDX", "Salewa")),
            LockedDoorMarker("int_lock_11sr", "Object 11SR Keycard", "Safe Room Exfil Keypad", "Burger King Toilet 2F", "Keycard activates the VIP underground Safe Room extraction in parking garage.", 460f, 540f, 2, estKeyPriceRubles = 4500000, notableLoot = listOf("Safe Room Extract Access", "Moonshine", "Bitcoins"))
        )

        return TarkovMap(
            id = "interchange",
            name = "Interchange",
            code = "INT",
            durationMinutes = 40,
            playerCount = "10-14 PMCs",
            difficulty = "Moderate",
            description = "Massive commercial shopping center. Three anchor stores (IDEA, Goshan, OLI) surrounding multi-story boutique corridors and tech hotspots.",
            floors = floors,
            structures = structures,
            lootSpawns = lootSpawns,
            extractions = extractions,
            bosses = bosses,
            quests = quests,
            vectorFeatures = vectorFeatures,
            lockedDoors = lockedDoors
        )
    }

    private fun createTheLabMap(): TarkovMap {
        val floors = listOf(
            FloorLevel("lab_b1", -1, "B1", "Sewage Channels & Technical Ducts", -45f),
            FloorLevel("lab_g", 0, "G", "Main Testing Laboratories & Catwalks", 0f),
            FloorLevel("lab_1f", 1, "1F", "Executive Offices & Keycard Rooms", 50f)
        )

        val structures = listOf(
            MapStructure("lab_complex", "TerraGroup Sub-Surface Lab", 200f, 200f, 800f, 800f, floors, "Underground corporate research installation with no insurance recovery")
        )

        val lootSpawns = listOf(
            LootSpawn("lab_loot_1", "Red Keycard Security Room", "High-tier weapon mods, Bitcoins, MPX, grenade boxes", Rarity.LEGENDARY, LootCategory.KEYS_CARDS, 380f, 340f, 1, "Red Keycard Vault", 3500000),
            LootSpawn("lab_loot_2", "Violet Keycard Room", "Thermal scopes, weapon crates, military flash drives", Rarity.LEGENDARY, LootCategory.KEYS_CARDS, 640f, 360f, 1, "Violet Keycard Cage", 2800000),
            LootSpawn("lab_loot_3", "Green Keycard Medical Lab", "Black keycard spawn, LEDX, injectors, defibs", Rarity.LEGENDARY, LootCategory.MEDICAL, 420f, 620f, 1, "Green Keycard Quarantine", 2600000),
            LootSpawn("lab_loot_4", "Black Keycard Experiment Dome", "Stimulant injector box, Meldonin, SJ6, Propital", Rarity.EPIC, LootCategory.MEDICAL, 520f, 580f, 0, "Dome Lab Counter", 1400000),
            LootSpawn("lab_loot_5", "Catwalk Technical Supply", "Virtex, VPX flash storage, Military circuit boards", Rarity.EPIC, LootCategory.TECH, 500f, 380f, 1, "PC Block", 920000),
            LootSpawn("lab_cont_safe1", "Manager Office Safe", "Bitcoins, Roler watches, Euros", Rarity.EPIC, LootCategory.CONTAINER, 530f, 360f, 1, "Safe", 550000),
            LootSpawn("lab_cont_weapon1", "Arsenal Heavy Weapon Box", "Silenced HK416, 60-rnd drums", Rarity.RARE, LootCategory.CONTAINER, 370f, 330f, 1, "Weapon Box", 280000),
            LootSpawn("lab_cont_med1", "Main Lab Medcase", "Propital, SJ6, Morphine injectors", Rarity.RARE, LootCategory.CONTAINER, 510f, 590f, 0, "Medcase", 220000)
        )

        val extractions = listOf(
            ExtractionPoint("lab_ex_1", "Hangar Gate", ExtractFaction.PMC_ONLY, ExtractType.SWITCH_POWER, "Must press alarm switch on upper walkway. Summons Raider wave.", 280f, 240f, 0),
            ExtractionPoint("lab_ex_2", "Parking Gate", ExtractFaction.PMC_ONLY, ExtractType.SWITCH_POWER, "Must trigger elevator gate button. Spawns heavily armed raiders.", 720f, 260f, 0),
            ExtractionPoint("lab_ex_3", "Sewage Conduit", ExtractFaction.PMC_ONLY, ExtractType.SWITCH_POWER, "Requires drainage pump switch activation. Water level lowers in 60s.", 500f, 780f, -1),
            ExtractionPoint("lab_ex_4", "Medical Block Elevator", ExtractFaction.PMC_ONLY, ExtractType.SWITCH_POWER, "Call elevator on panel; wait for chime.", 650f, 620f, 0),
            ExtractionPoint("lab_ex_5", "Ventilation Shaft", ExtractFaction.PMC_ONLY, ExtractType.ALWAYS_OPEN, "Requires dropping backpack.", 340f, 650f, -1)
        )

        val bosses = listOf(
            BossSpawn(
                "TerraGroup Raiders",
                "Elite Laboratory Mercenaries",
                100,
                "Spawn upon calling any elevator or gate extract",
                12,
                "HK416, Mk47 Mutant, Class 5 Korund armor, M995 / BP ammo",
                "High AI accuracy, coordinated squad pushes, deadly fragmentation grenades.",
                500f, 500f, 0
            )
        )

        val quests = listOf(
            QuestMarker("lab_q1", "The Guide", "Peacekeeper", "Survive and extract from The Lab as part of the cross-map marathon", null, 500f, 500f, 0)
        )

        val vectorFeatures = listOf(
            MapVectorFeature("lab_v_hull", VectorFeatureType.BUILDING_OUTLINE, 99, listOf(200f to 200f, 800f to 200f, 800f to 800f, 200f to 800f, 200f to 200f), "Laboratory Hull")
        )

        val lockedDoors = listOf(
            LockedDoorMarker("lab_lock_red", "TerraGroup Lab Keycard (Red)", "Red Keycard Arsenal", "Central Research 1F", "Most legendary locked room in Tarkov. Weapon racks, MPX, Bitcoin, LedX.", 380f, 340f, 1, isMarkedRoom = false, estKeyPriceRubles = 42000000, notableLoot = listOf("Bitcoins", "Modded M4A1", "LEDX", "High-tier Stims")),
            LockedDoorMarker("lab_lock_violet", "TerraGroup Lab Keycard (Violet)", "Violet Keycard Security", "Violet R&D Sector 1F", "Weapon cases, VPX, military tech, loose modded weapons.", 640f, 360f, 1, estKeyPriceRubles = 12500000, notableLoot = listOf("Weapon Cases", "Military Gyroscope", "VPX", "Bitcoins")),
            LockedDoorMarker("lab_lock_green", "TerraGroup Lab Keycard (Green)", "Green Keycard Quarantine Lab", "Green Hydroponics Sector 1F", "Black keycard loose spawn, multiple LEDX, Defibrillators, Ophthalmoscope.", 420f, 620f, 1, estKeyPriceRubles = 16000000, notableLoot = listOf("Black Keycard Spawn", "LEDX", "Ophthalmoscope", "Propital")),
            LockedDoorMarker("lab_lock_black", "TerraGroup Lab Keycard (Black)", "Black Keycard Experiment Dome", "Central Dome 1F", "Continuous injection supply: SJ6, Propital, ETG, Meldonin, Zagustin, AHF1.", 520f, 580f, 0, estKeyPriceRubles = 8500000, notableLoot = listOf("Endless Combat Stims", "LEDX", "SJ6 Stims")),
            LockedDoorMarker("lab_lock_blue", "TerraGroup Lab Keycard (Blue)", "Blue Keycard Medical Bay", "Blue Quarantine Sector 1F", "Medical cases, loose LedX table spawn, trauma surgery supplies.", 690f, 290f, 1, estKeyPriceRubles = 6800000, notableLoot = listOf("LEDX", "Medcases", "Defibrillator")),
            LockedDoorMarker("lab_lock_yellow", "TerraGroup Lab Keycard (Yellow)", "Yellow Keycard Catwalks Station", "Catwalks Control Room 2F", "Can disable the deafening parking/hangar raider alarm sirens. Safe and PC block.", 690f, 710f, 2, estKeyPriceRubles = 3200000, notableLoot = listOf("Alarm Shutdown Button", "Safe", "PC Block"))
        )

        return TarkovMap(
            id = "the_lab",
            name = "The Lab",
            code = "LAB",
            durationMinutes = 35,
            playerCount = "6-10 PMCs",
            difficulty = "Insane",
            description = "Subterranean laboratory complex underneath Tarkov center. No insurance return. Requires TerraGroup Labs Access Keycard. Highest value loot in the game.",
            floors = floors,
            structures = structures,
            lootSpawns = lootSpawns,
            extractions = extractions,
            bosses = bosses,
            quests = quests,
            vectorFeatures = vectorFeatures,
            lockedDoors = lockedDoors
        )
    }

    private fun createWoodsMap(): TarkovMap {
        val floors = listOf(FloorLevel("woods_g", 0, "G", "Dense Forest & Mountains", 0f))
        val structures = listOf(
            MapStructure("sawmill", "Lumber Sawmill (Shturman)", 440f, 440f, 580f, 580f, floors, "Central sawmill complex, Shturman sniper zone, lockbox"),
            MapStructure("usec_camp", "USEC Camp", 240f, 220f, 340f, 320f, floors, "Minefield borders, medical tents, weapon attachments")
        )
        val lootSpawns = listOf(
            LootSpawn("w_loot_1", "Shturman's Common Stash", "Red Rebel Ice Pick, Bitcoins, Keycards, SVDS", Rarity.LEGENDARY, LootCategory.VALUABLES, 510f, 510f, 0, "Shturman Stash Box", 2800000),
            LootSpawn("w_loot_2", "USEC Camp Med Tent", "LEDX spawn table, Morphine, Surgical kits", Rarity.EPIC, LootCategory.MEDICAL, 290f, 260f, 0, "Stretcher Table", 950000),
            LootSpawn("w_loot_3", "ZB-014 Bunker Locker", "60-round 5.45 mags, rare keys", Rarity.RARE, LootCategory.WEAPONS_AMMO, 180f, 680f, 0, "Secured Locker", 210000)
        )
        val extractions = listOf(
            ExtractionPoint("w_ex_1", "Outskirts", ExtractFaction.ALL, ExtractType.ALWAYS_OPEN, "Southwest perimeter dirt road corner.", 120f, 850f, 0),
            ExtractionPoint("w_ex_2", "Northern UN Roadblock", ExtractFaction.PMC_ONLY, ExtractType.ALWAYS_OPEN, "North checkpoint barricade.", 780f, 150f, 0),
            ExtractionPoint("w_ex_3", "ZB-014", ExtractFaction.PMC_ONLY, ExtractType.CONDITIONAL_SMOKE, "Requires ZB-014 key. Green smoke signal.", 180f, 690f, 0, hasGreenSmoke = true),
            ExtractionPoint("w_ex_4", "Bridge V-Ex", ExtractFaction.PMC_ONLY, ExtractType.CAR_PAID, "Requires 5,000 Roubles. Leaves after 60s.", 650f, 120f, 0, costRubles = 5000)
        )
        val bosses = listOf(
            BossSpawn("Shturman", "Woods Master Sniper", 35, "Lumber Sawmill & surrounding treeline", 2, "SVDS 7.62x54R, Red Rebel Ice Pick, Key", "Elite marksman. Fires 3-shot rapid bursts with high-pen ammo. Flanks quietly.", 510f, 490f, 0)
        )
        val quests = listOf(
            QuestMarker("w_q1", "Introduction", "Mechanic", "Find Jaeger's camp and retrieve encrypted message", null, 420f, 650f, 0)
        )
        val vectorFeatures = listOf(
            MapVectorFeature("w_v_lake", VectorFeatureType.WATER, 0, listOf(480f to 560f, 540f to 600f, 600f to 750f, 420f to 800f, 480f to 560f), "Pripyat Lake")
        )
        val lockedDoors = listOf(
            LockedDoorMarker("w_lock_shturman", "Shturman Stash Key", "Common Fund Box", "Sawmill Center 0F", "Shturman's locked metal cash box. Spawns Red Rebel ice pick, Bitcoins, SVDS.", 510f, 530f, 0, estKeyPriceRubles = 420000, notableLoot = listOf("Red Rebel Ice Pick", "Bitcoins", "SVDS Rifle")),
            LockedDoorMarker("w_lock_zb014", "ZB-014 Key", "ZB-014 Bunker Grate", "ZB-014 Bunker -1F", "Unlocks rear bunker security gate with 60-round 5.45 mags and weapon box.", 180f, 690f, 0, estKeyPriceRubles = 95000, notableLoot = listOf("60-rnd 5.45 Mags", "Weapon Box"), requiredForQuests = listOf("Ice Cream Cones"))
        )

        return TarkovMap("woods", "Woods", "WOD", 40, "9-14 PMCs", "Moderate", "Dense national park reserve around lake. Long range engagements, sniper rocks, and hidden minefields.", floors, structures, lootSpawns, extractions, bosses, quests, vectorFeatures, lockedDoors = lockedDoors)
    }

    private fun createShorelineMap(): TarkovMap {
        val floors = listOf(
            FloorLevel("shore_b1", -1, "B1", "Resort Basement & Pool", -40f),
            FloorLevel("shore_g", 0, "G", "Ground Level & Cottage Grounds", 0f),
            FloorLevel("shore_1f", 1, "1F", "Resort 1st Floor & Admin", 45f),
            FloorLevel("shore_2f", 2, "2F", "Resort 2nd Floor (LEDX Rooms)", 90f),
            FloorLevel("shore_3f", 3, "3F", "Resort 3rd Floor (Sanitar)", 135f)
        )
        val structures = listOf(
            MapStructure("resort_west", "Health Resort (West Wing)", 420f, 260f, 490f, 420f, floors, "West 301, West 216, LEDX & Graphic card spawns"),
            MapStructure("resort_east", "Health Resort (East Wing)", 550f, 260f, 620f, 420f, floors, "East 226/222, East 310, high-density valuable loot"),
            MapStructure("pier", "Pier & Lighthouse Offices", 510f, 760f, 620f, 880f, floors, "Safes, PC blocks, Pier boat extract")
        )
        val lootSpawns = listOf(
            LootSpawn("s_loot_1", "West Wing 301", "LEDX, GPUs, SV98, Golden Star, Ophthalmoscope", Rarity.LEGENDARY, LootCategory.MEDICAL, 440f, 290f, 3, "Bed Table & Balcony", 1900000),
            LootSpawn("s_loot_2", "East Wing 226", "LEDX, Bitcoins, Tetriz, MPX, Keycard spawns", Rarity.LEGENDARY, LootCategory.VALUABLES, 580f, 320f, 2, "Locker & Wardrobe", 1750000),
            LootSpawn("s_loot_3", "East Wing 310", "Bitcoins, Rolex, Golden Necklaces on blankets", Rarity.EPIC, LootCategory.VALUABLES, 590f, 280f, 3, "Bed Blankets", 650000),
            LootSpawn("s_cont_safe1", "Pier Management Safe 1", "Bitcoins, Roler watches, Cash", Rarity.EPIC, LootCategory.CONTAINER, 530f, 790f, 1, "Safe", 310000),
            LootSpawn("s_cont_pc1", "Admin Wing PC Block", "Graphics cards, Flash drives, CPU", Rarity.EPIC, LootCategory.CONTAINER, 490f, 340f, 1, "PC Block", 370000),
            LootSpawn("s_cont_jacket1", "Weather Station 2F Jacket", "Health Resort Keys, Keycards", Rarity.EPIC, LootCategory.CONTAINER, 740f, 670f, 1, "Jacket", 260000)
        )
        val extractions = listOf(
            ExtractionPoint("s_ex_1", "Tunnel", ExtractFaction.ALL, ExtractType.ALWAYS_OPEN, "Far western highway coastal tunnel.", 110f, 680f, 0),
            ExtractionPoint("s_ex_2", "Road to Customs", ExtractFaction.ALL, ExtractType.ALWAYS_OPEN, "Northeastern industrial gate.", 890f, 320f, 0),
            ExtractionPoint("s_ex_3", "Pier Boat", ExtractFaction.ALL, ExtractType.CONDITIONAL_SMOKE, "Buoy light blinking when boat is active.", 580f, 860f, 0, hasGreenSmoke = true),
            ExtractionPoint("s_ex_4", "Path to Lighthouse", ExtractFaction.ALL, ExtractType.ALWAYS_OPEN, "Northwestern mountain ridge trail.", 150f, 180f, 0)
        )
        val bosses = listOf(
            BossSpawn("Sanitar", "Corrupt Resort Physician", 30, "Health Resort, Pier Offices, or Cottages", 2, "VSS Vintorez, Obdolbos cocktail, Sanitar Key", "Injects powerful combat stimulants into himself and guards. Heals teammates mid-battle.", 510f, 320f, 1)
        )
        val quests = listOf(
            QuestMarker("s_q1", "Spa Tour - Part 1", "Peacekeeper", "Kill Scavs with headshots on Shoreline", null, 500f, 500f, 0)
        )
        val vectorFeatures = listOf(
            MapVectorFeature("s_v_sea", VectorFeatureType.WATER, 0, listOf(100f to 800f, 500f to 800f, 900f to 800f), "Black Sea Coast")
        )
        val lockedDoors = listOf(
            LockedDoorMarker("sho_lock_e226", "Sanatorium East Wing 226 Key", "East Wing Room 226/222", "Health Resort East Wing 2F", "Connected rooms with multiple LEDX spawns, Graphics Cards, Tetriz, and weapon box.", 580f, 320f, 2, estKeyPriceRubles = 1450000, notableLoot = listOf("LEDX", "Graphics Card", "Tetriz", "Weapon Box")),
            LockedDoorMarker("sho_lock_e310", "Sanatorium East Wing 310 Key", "East Wing Room 310", "Health Resort East Wing 3F", "Known as the gold room: Bitcoins, Rolers, Gold Chains on the bed blankets.", 590f, 280f, 3, estKeyPriceRubles = 980000, notableLoot = listOf("Bitcoins", "Roler Watches", "Gold Chains")),
            LockedDoorMarker("sho_lock_w301", "Sanatorium West Wing 301 Key", "West Wing Room 301/304", "Health Resort West Wing 3F", "Premier West Wing room: LEDX on desk, 2x PC blocks, SV-98 sniper spawn.", 440f, 290f, 3, estKeyPriceRubles = 1200000, notableLoot = listOf("LEDX", "2x PC Blocks", "SV-98 Sniper"), requiredForQuests = listOf("Spa Tour - Part 4")),
            LockedDoorMarker("sho_lock_cottage", "Sanitar Cottage Safe Key", "Cottage Safe Room", "Sanitar Cottages 2F", "Two safes with high tier valuables, cash, and PC block.", 320f, 580f, 1, estKeyPriceRubles = 420000, notableLoot = listOf("2x Safes", "PC Block", "Moonshine"))
        )
        return TarkovMap("shoreline", "Shoreline", "SHO", 45, "10-13 PMCs", "Moderate", "Large coastal region with Azure Coast luxury health resort sanatorium at center. Elite medical and valuable loot.", floors, structures, lootSpawns, extractions, bosses, quests, vectorFeatures, lockedDoors = lockedDoors)
    }

    private fun createLighthouseMap(): TarkovMap {
        val floors = listOf(FloorLevel("lh_g", 0, "G", "Coastline & Treatment Plant", 0f), FloorLevel("lh_1f", 1, "1F", "Rogue Roof Nests & Chalets", 45f))
        val structures = listOf(
            MapStructure("wtp", "Water Treatment Plant (Rogues)", 450f, 180f, 650f, 400f, floors, "Mounted machine guns, AGS-30 grenade turrets, Rogue patrol"),
            MapStructure("chalet", "Luxury Mountain Chalet", 600f, 550f, 750f, 680f, floors, "Merin trunk key, Virtex, Bitcoin spawns")
        )
        val lootSpawns = listOf(
            LootSpawn("lh_loot_1", "Water Treatment Building 1 Safe", "Bitcoins, Moonshine, Military cables", Rarity.EPIC, LootCategory.VALUABLES, 510f, 240f, 1, "Safe", 820000),
            LootSpawn("lh_loot_2", "Merin Car Trunk", "Bitcoins, Prokill, Keycards, Gold Chains", Rarity.LEGENDARY, LootCategory.VALUABLES, 620f, 620f, 0, "Car Trunk", 1650000),
            LootSpawn("lh_cont_safe1", "Chalet 2F Bedroom Safe", "Bitcoins, Rolex, Euro stacks", Rarity.EPIC, LootCategory.CONTAINER, 670f, 610f, 1, "Safe", 380000),
            LootSpawn("lh_cont_tool1", "Industrial Technical Crate", "Military cables, Phased array element", Rarity.RARE, LootCategory.CONTAINER, 480f, 220f, 0, "Toolbox", 160000)
        )
        val extractions = listOf(
            ExtractionPoint("lh_ex_1", "Southern Road", ExtractFaction.ALL, ExtractType.ALWAYS_OPEN, "Paved highway southern roadblock.", 510f, 900f, 0),
            ExtractionPoint("lh_ex_2", "Mountain Pass", ExtractFaction.PMC_ONLY, ExtractType.RED_REBEL_PARACORD, "Requires Red Rebel + Paracord.", 780f, 520f, 1)
        )
        val bosses = listOf(
            BossSpawn("Rogues", "USEC Ex-Military", 100, "Water Treatment Plant Roofs and Courtyard", 8, "M4A1, SCAR-L, Class 5 Armor", "Stationary gunners lock on from 300+ meters. Attack at night or from blind angles.", 540f, 280f, 1)
        )
        val quests = listOf(
            QuestMarker("lh_q1", "Overpopulation", "Peacekeeper", "Neutralize scavs at the Chalet", null, 680f, 610f, 0)
        )
        val vectorFeatures = listOf(
            MapVectorFeature("lh_v_coast", VectorFeatureType.WATER, 0, listOf(200f to 100f, 250f to 500f, 300f to 900f), "Ocean")
        )
        val lockedDoors = listOf(
            LockedDoorMarker("lh_lock_merin", "Merin Car Trunk Key", "Merin Car Trunk", "Southern Highway 0F", "Locked car trunk with Bitcoins, Prokill medals, virtex processors, and keycards.", 620f, 620f, 0, estKeyPriceRubles = 1650000, notableLoot = listOf("Bitcoins", "Virtex", "VPX", "Keycards")),
            LockedDoorMarker("lh_lock_chalet", "Chalet Master Bedroom Key", "Chalet 2F Safe Room", "Luxury Mountain Chalet 2F", "Master bedroom with steel safe, Bitcoins, jewelry, and weapons.", 670f, 610f, 1, estKeyPriceRubles = 380000, notableLoot = listOf("Safe", "Bitcoins", "Roler Watch"))
        )
        return TarkovMap("lighthouse", "Lighthouse", "LGT", 40, "9-12 PMCs", "Hard", "Scenic coastal highway featuring Cape Dalniy lighthouse, luxury cottages, and heavily fortified Rogue Water Treatment Plant.", floors, structures, lootSpawns, extractions, bosses, quests, vectorFeatures, lockedDoors = lockedDoors)
    }

    private fun createGroundZeroMap(): TarkovMap {
        val floors = listOf(
            FloorLevel("gz_b1", -1, "B1", "Nakatani Underground Parking & Wine Cellar", -40f),
            FloorLevel("gz_g", 0, "G", "Street Level Plaza & Main Lobbies", 0f),
            FloorLevel("gz_1f", 1, "1F", "TerraGroup Lab Offices & Medbay", 45f),
            FloorLevel("gz_2f", 2, "2F", "Skytower Executive Suite & Sniper Vantage", 90f)
        )
        val structures = listOf(
            MapStructure("terragroup_gz", "TerraGroup HQ Tower", 400f, 350f, 580f, 520f, floors, "Scientists' office, hard drive quest objectives, medical bay"),
            MapStructure("capital_insight", "Capital Insight Bank", 200f, 320f, 350f, 480f, floors, "Financial offices, cash registers, teller safes, computer blocks"),
            MapStructure("empire_tower", "Empire Skytower", 630f, 340f, 800f, 500f, floors, "Luxury hotel suites, executive conference room, sniper vantage"),
            MapStructure("nakatani_center", "Nakatani Parking & Transit", 280f, 600f, 440f, 720f, floors, "Underground parking garage, extraction stairs, car boots"),
            MapStructure("fusion_cafe", "Fusion Restaurant & Plaza", 580f, 600f, 720f, 700f, floors, "Food provisions, duffle bags, dead scav bodies")
        )
        val lootSpawns = listOf(
            LootSpawn("gz_loot_1", "TerraGroup Scientist Office", "Encrypted hard drive, Intel folder, Stims", Rarity.EPIC, LootCategory.TECH, 480f, 420f, 1, "Scientist Desk", 750000),
            LootSpawn("gz_loot_2", "Nakatani Parking Trunk", "Loose weapon mods, cash, duffle bag", Rarity.RARE, LootCategory.WEAPONS_AMMO, 320f, 650f, -1, "Vehicle Trunk", 180000),
            LootSpawn("gz_loot_3", "Capital Insight Vault", "Roubles, Gold chains, Bitcoins, Prokill", Rarity.LEGENDARY, LootCategory.VALUABLES, 270f, 400f, 0, "Safe", 920000),
            LootSpawn("gz_loot_4", "Empire Tower 2F Suite", "Modded Vector 9mm, High-pen ammo, PC block", Rarity.EPIC, LootCategory.WEAPONS_AMMO, 710f, 420f, 2, "Executive Suite Desk", 580000),
            LootSpawn("gz_loot_5", "Wine Store Basement", "Tarkovskaya Vodka, Moonshine, Grenade box", Rarity.RARE, LootCategory.PROVISIONS, 430f, 470f, -1, "Cellar Crates", 220000),
            LootSpawn("gz_loot_6", "TerraGroup Medbay 1F", "Salewa, CMS surgical kit, Morphine, Medcase", Rarity.EPIC, LootCategory.MEDICAL, 520f, 430f, 1, "Medcase", 340000),
            // Dedicated Containers
            LootSpawn("gz_cont_safe1", "Capital Insight Safe 1", "Bitcoins, Cash, Prokill", Rarity.EPIC, LootCategory.CONTAINER, 260f, 380f, 0, "Safe", 310000),
            LootSpawn("gz_cont_safe2", "Capital Insight Safe 2", "Euros, Roubles, Gold Necklaces", Rarity.RARE, LootCategory.CONTAINER, 280f, 380f, 0, "Safe", 210000),
            LootSpawn("gz_cont_pc1", "TerraGroup Lab PC Block", "Graphics Card, Flash Drive, SSD", Rarity.EPIC, LootCategory.CONTAINER, 470f, 410f, 1, "PC Block", 410000),
            LootSpawn("gz_cont_jacket1", "Fusion Cafe Hallway Jacket", "TerraGroup Office Key, Car Trunk Key", Rarity.RARE, LootCategory.CONTAINER, 610f, 630f, 0, "Jacket", 180000),
            LootSpawn("gz_cont_med1", "TerraGroup Medcase", "Morphine, Salewa, Golden Star", Rarity.RARE, LootCategory.CONTAINER, 530f, 420f, 1, "Medcase", 150000),
            LootSpawn("gz_cont_tool1", "Nakatani Parking Toolbox", "Elite Pliers, Screwdriver, Wires", Rarity.COMMON, LootCategory.CONTAINER, 350f, 640f, -1, "Toolbox", 75000),
            LootSpawn("gz_cont_dead1", "Mira Avenue Dead Scav", "Casualty body with backpack, grenades", Rarity.COMMON, LootCategory.CONTAINER, 720f, 470f, 0, "Dead Scav", 65000),
            LootSpawn("gz_cont_duffle1", "Wine Store Duffle Bag", "Sugar, Bolts, Vodka, Gunpowder", Rarity.COMMON, LootCategory.CONTAINER, 440f, 480f, -1, "Duffle Bag", 90000)
        )
        val extractions = listOf(
            ExtractionPoint("gz_ex_1", "Mira Avenue (Green Flare)", ExtractFaction.ALL, ExtractType.CONDITIONAL_SMOKE, "Must fire Green Flare before crosswalk or sniper will eliminate you.", 780f, 380f, 0, hasFlares = true),
            ExtractionPoint("gz_ex_2", "Police Car V-Ex", ExtractFaction.PMC_ONLY, ExtractType.CAR_PAID, "Requires 5,000 Roubles. 60-second timer.", 220f, 440f, 0, costRubles = 5000),
            ExtractionPoint("gz_ex_3", "Nakatani Basement Stairs", ExtractFaction.ALL, ExtractType.ALWAYS_OPEN, "Underground stairs exit.", 340f, 690f, -1)
        )
        val bosses = listOf(
            BossSpawn("Kollontay Patrol", "Former MVD Officer", 20, "Ground Zero Avenue & TerraGroup Lobby", 4, "RPD Machine Gun, Police Baton", "Uses panic baton aura to induce weapon jams on players.", 460f, 450f, 0)
        )
        val quests = listOf(
            QuestMarker("gz_q1", "First in Line", "Therapist", "Find the medical station at the TerraGroup building", null, 460f, 420f, 1),
            QuestMarker("gz_q2", "Shooting Cans", "Prapor", "Locate machine gun nest and grenade launcher nest", null, 520f, 390f, 0),
            QuestMarker("gz_q3", "Saving the Mole", "Mechanic", "Retrieve the TerraGroup scientist's encrypted hard drive", null, 480f, 420f, 1)
        )
        val vectorFeatures = listOf(
            MapVectorFeature("gz_v_st_main", VectorFeatureType.ROAD, 0, listOf(100f to 520f, 500f to 520f, 900f to 520f), "Primorsky Avenue"),
            MapVectorFeature("gz_v_st_cross", VectorFeatureType.ROAD, 0, listOf(500f to 150f, 500f to 520f, 500f to 850f), "Mira Avenue"),
            MapVectorFeature("gz_v_trench", VectorFeatureType.HAZARD_ZONE, 0, listOf(360f to 490f, 640f to 490f), "Anti-Vehicle Barricade Line")
        )
        val lockedDoors = listOf(
            LockedDoorMarker("gz_lock_terragroup", "TerraGroup Office Key", "Executive Office", "TerraGroup HQ Tower 1F", "Saving the Mole quest. Encrypted hard drive, VPX, Bitcoins, Flash drives.", 480f, 420f, 1, estKeyPriceRubles = 320000, notableLoot = listOf("Encrypted Hard Drive", "VPX", "Bitcoins", "SSDs"), requiredForQuests = listOf("Saving the Mole")),
            LockedDoorMarker("gz_lock_nakatani", "Nakatani Key", "Basement Utility Room", "Nakatani Parking B1", "Underground utility maintenance room with weapon boxes and medical supply bags.", 310f, 660f, -1, estKeyPriceRubles = 75000, notableLoot = listOf("Heavy Weapon Box", "Toolbox", "Medcase")),
            LockedDoorMarker("gz_lock_bank", "Capital Insight Cashier Key", "Bank Teller Vault", "Capital Insight Bank 1F", "Ante-room safes, money stacks, gold chains, roler watches.", 250f, 390f, 0, estKeyPriceRubles = 140000, notableLoot = listOf("2x Steel Safes", "Money Stacks", "Gold Chains")),
            LockedDoorMarker("gz_lock_skytower", "Skytower Management Key", "Empire Tower 2F Suite", "Empire Skytower 2F", "Executive management suite with PC block, duffle bag, and high-pen ammunition.", 720f, 410f, 2, estKeyPriceRubles = 95000, notableLoot = listOf("PC Block", "Duffle Bag", "Modded 9mm Weapon"))
        )
        return TarkovMap("ground_zero", "Ground Zero", "GZ", 35, "10-12 PMCs", "Low to High (Split Queues)", "Beginner-focused high-rise downtown streetscape. Initial staging ground for early PMC contract storylines.", floors, structures, lootSpawns, extractions, bosses, quests, vectorFeatures, lockedDoors = lockedDoors)
    }

    val ammoList: List<AmmoEntry> by lazy {
        listOf(
            AmmoEntry("556_m995", "5.56x45mm NATO", "M995 (Tungsten Core)", damage = 42, penetration = 53, armorDamagePct = 58, fragChancePct = 32, projectileVelocity = 1010, classEffectiveness = listOf(6, 6, 6, 6, 6, 5)),
            AmmoEntry("556_m855a1", "5.56x45mm NATO", "M855A1 (Steel Penetrator)", damage = 49, penetration = 44, armorDamagePct = 52, fragChancePct = 34, projectileVelocity = 945, classEffectiveness = listOf(6, 6, 6, 6, 5, 3)),
            AmmoEntry("556_m856a1", "5.56x45mm NATO", "M856A1 (Tracer)", damage = 52, penetration = 38, armorDamagePct = 51, fragChancePct = 33, projectileVelocity = 940, classEffectiveness = listOf(6, 6, 6, 5, 3, 1)),
            AmmoEntry("556_m855", "5.56x45mm NATO", "M855 (FMJ)", damage = 50, penetration = 30, armorDamagePct = 44, fragChancePct = 36, projectileVelocity = 922, classEffectiveness = listOf(6, 6, 5, 2, 1, 0)),

            AmmoEntry("545_bs", "5.45x39mm", "BS (Special Tungsten)", damage = 43, penetration = 51, armorDamagePct = 57, fragChancePct = 17, projectileVelocity = 830, classEffectiveness = listOf(6, 6, 6, 6, 6, 4)),
            AmmoEntry("545_bt", "5.45x39mm", "BT gs (Armor-Piercing Tracer)", damage = 44, penetration = 42, armorDamagePct = 49, fragChancePct = 16, projectileVelocity = 880, classEffectiveness = listOf(6, 6, 6, 6, 4, 2)),
            AmmoEntry("545_bp", "5.45x39mm", "BP gs (Steel Core)", damage = 46, penetration = 35, armorDamagePct = 45, fragChancePct = 15, projectileVelocity = 890, classEffectiveness = listOf(6, 6, 6, 4, 2, 0)),

            AmmoEntry("76251_m61", "7.62x51mm NATO", "M61 (Depleted Uranium/Tungsten)", damage = 75, penetration = 64, armorDamagePct = 83, fragChancePct = 13, projectileVelocity = 849, classEffectiveness = listOf(6, 6, 6, 6, 6, 6)),
            AmmoEntry("76251_m80", "7.62x51mm NATO", "M80 (Standard Ball)", damage = 80, penetration = 41, armorDamagePct = 66, fragChancePct = 17, projectileVelocity = 833, classEffectiveness = listOf(6, 6, 6, 6, 4, 2)),

            AmmoEntry("76239_bp", "7.62x39mm", "BP gzh (Armor-Piercing)", damage = 60, penetration = 48, armorDamagePct = 63, fragChancePct = 12, projectileVelocity = 730, classEffectiveness = listOf(6, 6, 6, 6, 5, 3)),
            AmmoEntry("76239_ps", "7.62x39mm", "PS gzh (Steel Core)", damage = 57, penetration = 35, armorDamagePct = 52, fragChancePct = 25, projectileVelocity = 700, classEffectiveness = listOf(6, 6, 6, 4, 2, 0)),

            AmmoEntry("76254_snb", "7.62x54mmR", "SNB (Special Sniper Armor-Piercing)", damage = 75, penetration = 62, armorDamagePct = 82, fragChancePct = 8, projectileVelocity = 870, classEffectiveness = listOf(6, 6, 6, 6, 6, 6)),
            AmmoEntry("919_ap63", "9x19mm Parabellum", "AP 6.3 (Armor Piercing)", damage = 52, penetration = 30, armorDamagePct = 48, fragChancePct = 5, projectileVelocity = 392, classEffectiveness = listOf(6, 6, 5, 2, 1, 0)),
            AmmoEntry("919_pbp", "9x19mm Parabellum", "PBP gzh (+P+ High Velocity)", damage = 52, penetration = 39, armorDamagePct = 53, fragChancePct = 12, projectileVelocity = 460, classEffectiveness = listOf(6, 6, 6, 5, 3, 1))
        )
    }

    val keysList: List<KeyEntry> by lazy {
        listOf(
            KeyEntry("k_314", "Dorm Room 314 Marked Key", "customs", "Customs", "3-Story Dorms, 3rd Floor, Room 314", "3F", 2850000, listOf("The Cult - Part 2"), listOf("Bitcoins, Keycards, Weapon Cases, Spear Rifle")),
            KeyEntry("k_kiba", "KIBA Arms Outer Door Key", "interchange", "Interchange", "ULTRA Mall 1F Center KIBA Store", "1F", 750000, listOf("Gunsmith - Part 11"), listOf("Modded SOPMOD M4, Thermal Scopes, Weapon racks")),
            KeyEntry("k_red", "TerraGroup Labs Red Keycard", "the_lab", "The Lab", "Central Research Lab 1F Security Room", "1F", 42000000, listOf("Collector"), listOf("Top-tier loot in the game, ammo boxes, MPX, valuables")),
            KeyEntry("k_d2", "RB-PKPM Reserve Marked Key", "reserve", "Reserve", "Black Pawn Underground Tunnel", "B1", 1650000, listOf("The Bunker"), listOf("Cases, Modified weapons, Bitcoins")),
            KeyEntry("k_w301", "Health Resort West Wing 301 Key", "shoreline", "Shoreline", "West Wing 3rd Floor Room 301", "3F", 1200000, listOf("Spa Tour - Part 4"), listOf("LEDX, Graphics Cards (GPU), Ophthalmoscopes")),
            KeyEntry("k_chek15", "Chekannaya 15 Apartment Key", "streets", "Streets of Tarkov", "Chekannaya Street Residential 2F", "2F", 3400000, listOf("You\'ve Got Mail"), listOf("Marked apartment loot, Keycards, Moonshine")),
            KeyEntry("k_factory", "Factory Emergency Exit Key", "factory", "Factory & Customs", "Factory Gate 0, Cellars, Customs Shortcut", "G", 180000, listOf("Bad Rep Evidence"), listOf("Quick instant extraction from Factory and ZB-1012 shortcut"))
        )
    }
}
