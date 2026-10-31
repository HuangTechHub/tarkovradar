# TarkovRadar: Interactive 3D Maps & In-Raid Tactical Companion

A high-performance, touch-optimized Android companion application for *Escape From Tarkov* players. Delivers layered isometric 3D navigation for complex multi-story structures, real-time loot spawn markers filtered by rarity and category, live extraction point tracking with conditional requirements, scav boss locators, quest guides, ballistic ammo charts, and full offline functionality designed for rapid access during high-stakes raids.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following key decisions were confirmed during clarification and shape the architecture:

- **Confirmed Decision 1 (3D Navigation & Multi-Layer System)**: Implemented as a high-speed layered isometric 2.5D/3D tactical canvas engine with interactive floor elevation toggles, smooth elevation separation, pinch-to-zoom, pan, perspective tilt, and single-tap floor slicing (B1, Ground, 1F, 2F, 3F, Roof).
- **Confirmed Decision 2 (Map Coverage)**: Complete out-of-the-box catalog of all major official Tarkov raid locations: Customs, Factory, Reserve, Streets of Tarkov, Interchange, The Lab, Woods, Shoreline, Lighthouse, and Ground Zero.
- **Confirmed Decision 3 (Raid Utility Suite)**: Comprehensive tactical toolkit including an active raid countdown timer, extraction checklist (PMC/Scav/Co-op/Paid Car), scav boss spawn radar with guard loadouts, quest objective overlays, key unlock guides, interactive ballistics chart (damage vs. armor penetration), and offline flea market value reference.
- **Confirmed Decision 4 (100% Offline Capability)**: All map vector blueprints, multi-story floor layouts, loot spawn tables, extraction conditions, and ballistic databases are pre-bundled and indexed locally with Room database persistence for custom user pins, raid history, and favorite extracts.

---

## 1. Overview & Core Concept

### What It Does
TarkovRadar gives PMC and Scav operators an instant, zero-latency situational awareness HUD. Players can inspect complex multi-floor complexes (such as Reserve's underground bunkers, Interchange's Ultra Mall, Customs' Dorms, or Factory's catwalks), toggle floor levels to isolate indoor vs. outdoor loot, filter spawn markers by rarity (LedX, GPUs, Bitcoins, Keycards, Intelligence, High-Tier Military Tech), check extract availability and prerequisites (e.g. 5000 Roubles, Paracord + Red Rebel, Friendly Scav), and look up ammo penetration tables on the fly.

### Target Audience & Persona
*Escape From Tarkov* players who need instant answers during intense 20-to-50 minute raids where taking hands off the mouse/keyboard or tabbed-out browser lag can result in sudden death. The UI is designed for one-thumb or quick two-tap interactions with oversized targets, high-contrast dark military styling, and zero reliance on spotty cellular/Wi-Fi connection.

### Key Value
- **Zero-Latency Multi-Floor Intelligence**: No waiting for heavy web embeds; instant floor toggles reveal exact room numbers, locked doors, and container types.
- **Loot Prioritization**: Instant filter chips highlight high-value items (Tier S/A/B) so players know whether an area is worth the risk.
- **Extract Certainty**: Visual status markers for guaranteed extracts, conditional extracts, green smoke flares, and vehicle extracts.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Raid Selection**: Browse maps with tactical thumbnails, raid duration, player counts, difficulty, and weather/lighting indicators. One tap launches directly into the map canvas.
2. **Interactive 3D Navigation**: Pan, pinch-zoom, and tilt the map. Use the floating elevation ladder on the right screen edge to isolate specific floors (e.g., *Customs 3-Story Dorms: 2nd Floor*) or expand all layers with 3D isometric vertical spacing.
3. **Loot & Intel Filtering**: Bottom sheet and quick filter pills allow toggling: High Value, Keys & Keycards, Medical, Technical, Weapons/Ammo, Scav Bosses, and Extractions.
4. **In-Raid HUD Mode**: Activate a persistent top HUD strip showing raid timer countdown, selected extracts with required gear, and quick-note scratchpad.
5. **Tactical Reference Center**: Instant tab switch to Ballistics Calculator (calibers, pen values, damage thresholds against Class 1–6 armor) and Key Directory with door locations and expected loot value.

### Visual Identity & Theme
- **Aesthetic Direction**: Tactical Militaristic HUD / Rugged Terminal. Dark charcoal and slate backgrounds reminiscent of ruggedized military field tablets and thermal optics.
- **Color Palette**:
  - `Background`: Ultra-dark slate `#0D1117` and rugged surface `#161B22`
  - `Surface High`: Elevated card slate `#21262D` with subtle borders `#30363D`
  - `Primary Accent`: Tactical Amber `#FF9F1C` (for primary actions, active filters, and key highlights)
  - `Tactical Green`: Night-vision phosphorescent emerald `#2EC4B6` / `#39FF14` (for open extracts and PMC spawns)
  - `Hazard Red / Alert`: Warning crimson `#E63946` (for Scav Boss spawn zones, sniper boundaries, and minefields)
  - `Loot Rarity Tiers`:
    - *Legendary (S-Tier)*: Red/Gold `#FFD700` (LEDX, Red Keycard, Bitcoin, GPU)
    - *Epic (A-Tier)*: Purple `#BD00FF` (Moonshine, Intel, High-tier keys, Mil-tech)
    - *Rare (B-Tier)*: Cyan `#00E5FF` (Salewa, Golden Star, Injectors, Suppressors)
    - *Common (C-Tier)*: Light Steel `#8B949E` (Toolboxes, jackets, weapon crates)
- **Typography & Hierarchy**: Monospace and bold sans-serif pairings (clean tactical headings, tabular numbers for timers and coordinates, crisp high-legibility labels at 11sp–14sp).
- **Component Styling & Layout**: Edge-to-edge support with safe insets, tactile 48dp+ tap targets for sweaty-hand gaming sessions, floating tactical quick-toggles, and fluid animated transitions.

---

## 3. Key Product Decisions & Trade-Offs

### Decision 1: Custom Vector Canvas Isometric 3D Engine vs Heavy 3D GL Engine
- **Chosen Approach**: Native Jetpack Compose Canvas rendering engine with 2.5D/3D affine transformation matrix (isometric projection, elevation offset stacking, dynamic z-ordering, and layer clipping).
- **Why**: Native Compose Canvas is lightning fast (rock-solid 60fps), lightweight (no multi-hundred-megabyte Unity or external 3D engine overhead), consumes minimal battery, and supports pinch-to-zoom, fling pan, and multi-layer projection flawlessly.
- **Alternatives Considered**: WebView loading third-party web maps (causes lag, fails offline, unreliable touch handling) or Filament/Sceneview (adds massive binary bloat and high GPU battery drain during gaming).

### Decision 2: Offline-First Bundled Geometry & Metadata with Room DB Persistence
- **Chosen Approach**: Built-in comprehensive vector layouts, coordinate models, and loot manifests for all 10 EFT locations, backed by Room database for user custom waypoints, key inventory, raid extract bookmarks, and offline ballistics tables.
- **Why**: EFT players frequently play on machines with second-screen phones on airplane/offline mode or spotty Wi-Fi. The app must never fail or show a spinner when checking an extract in a gunfight.
- **Alternatives Considered**: Cloud-only API (fails when internet drops or backend servers throttle).

### Decision 3: Touch-Optimized Raid Action HUD
- **Chosen Approach**: Sticky top/bottom overlay with minimal obstruction: collapsible raid timer, 1-tap floor elevation switcher, single-tap filter chips, and full-screen map mode with quick dismiss.
- **Why**: Keeps 85%+ of screen estate purely dedicated to tactical map geometry while keeping critical extract information 1 tap away.

---

## 4. Technical Architecture & Data Strategy

### System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                             MainActivity & Theme                            │
│                  (Tactical Dark Theme, Edge-to-Edge Insets)                 │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
┌──────────────────────────────────────▼──────────────────────────────────────┐
│                            TarkovRadarApp State                             │
│                  (Navigation: Maps, Ballistics, Keys, Raid HUD)             │
└───────┬──────────────────────────────┼──────────────────────────────┬───────┘
        │                              │                              │
┌───────▼────────────────┐   ┌─────────▼──────────────┐   ┌───────────▼───────────┐
│     MapCanvasView      │   │  BallisticsScreen      │   │    KeysDirectory      │
│ ┌────────────────────┐ │   │ • Caliber selector     │   │ • Key search          │
│ │ Layered 3D Engine  │ │   │ • Penetration vs Armor │   │ • Lock location       │
│ │ • Elevation Stacks │ │   │ • Damage & Frag chance │   │ • Behind-door loot    │
│ │ • Multi-Floor Slice│ │   └────────────────────────┘   └───────────────────────┘
│ │ • Rarity Filtering │ │
│ │ • Extract Checkers │ │
│ └────────────────────┘ │
│ ┌────────────────────┐ │
│ │ In-Raid Floating   │ │
│ │ Toolbars & Timer   │ │
│ └────────────────────┘ │
└───────────────┬────────┘
                │
┌───────────────▼─────────────────────────────────────────────────────────────┐
│                           TarkovMapViewModel                                │
│    (Active Map, Zoom/Pan/Tilt State, Floor Level, Filter State, Raid Timer) │
└───────┬──────────────────────────────┬──────────────────────────────┬───────┘
        │                              │                              │
┌───────▼────────────────┐   ┌─────────▼──────────────┐   ┌───────────▼───────────┐
│  TarkovRepository      │   │   Room Database        │   │  Embedded Data Assets │
│  • Map coordinates     │   │   • Custom User Pins   │   │  • 10 Map Geometries  │
│  • Loot spawn points   │   │   • Extract Bookmarks  │   │  • Multi-floor Plans  │
│  • Extract conditions  │   │   • Key Possession     │   │  • Ballistics Matrix  │
│  • Boss spawn tables   │   │   • Offline Cache      │   │  • Rarity Loot Tables │
└────────────────────────┘   └────────────────────────┘   └───────────────────────┘
```

### Core Entities & State

- **`TarkovMap`**: ID, name, lore, raid time (minutes), player count, difficulty, available floors (`FloorLevel`), bounds, extraction points, loot spawns, boss spawns, quest objectives.
- **`FloorLevel`**: ID, elevation index (-2 to +3), floor name ("Basement B2", "Basement B1", "Ground Floor", "1st Floor", "2nd Floor", "Roof"), isometric offset `(dx, dy)`.
- **`LootSpawnPoint`**: ID, name, category (Medical, Keys, Tech, Weapons, Valuables), rarity (`LEGENDARY`, `EPIC`, `RARE`, `COMMON`), coordinates `(x, y, floorId)`, container type (Safe, Duffle, PC Block, Wooden Crate, Loose Loot), estimated value.
- **`ExtractionPoint`**: ID, name, faction (`PMC_ONLY`, `SCAV_ONLY`, `ALL`), extract type (`ALWAYS_OPEN`, `CONDITIONAL_SMOKE`, `CAR_PAID`, `SPECIAL_EQUIPMENT`), requirements description (e.g., "5,000 Roubles", "Red Rebel + Paracord, No Armor Vest").
- **`BossSpawn`**: Boss name (e.g., "Killa", "Reshala", "Glukhar", "Tagilla"), spawn rate percentage, spawn locations, guard count, signature loot.
- **`AmmoEntry`**: Caliber, bullet name, damage, penetration power, armor class effectiveness (Class 1 through 6).

---

## 5. Verification & Implementation Steps

1. **Configure Metadata & Theme**: Update `metadata.json` and `strings.xml` with app title "TarkovRadar", configure tactical dark theme with amber/emerald accents in `Color.kt` and `Theme.kt`.
2. **Build Data Models & Embedded Offline Catalog**: Create comprehensive Tarkov maps data (Customs, Reserve, Factory, Streets, Interchange, Labs, etc.), detailed multi-story structures (Dorms, Bunker, Catwalks, Techlight), loot matrices, extracts, and ammo ballistics tables.
3. **Build Room Persistence Layer**: Entity & DAO for user-saved custom map waypoints, extract checklists, and raid session notes.
4. **Develop 3D Multi-Layer Canvas Engine**: Implement interactive gesture canvas (pan, pinch-to-zoom, isometric perspective angle, floor elevation vertical separation, floor solo isolation, and hit-testing for loot/extract markers).
5. **Develop In-Raid Tactical HUD**: Floating floor switcher, active raid countdown timer with warning alerts, rarity filter chips, category toggle sheet, and extract requirement inspector dialog.
6. **Develop Companion Tool Tabs**:
   - *Ballistics Chart*: Interactive caliber filter and penetration ratings against Armor Classes 1–6.
   - *Key & Quest Directory*: Fast search for keys, unlock doors, and map quest items.
7. **Verify & Compile**: Run `compile_applet` to ensure error-free compilation and clean layout execution.
