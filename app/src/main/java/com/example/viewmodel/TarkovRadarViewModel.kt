package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TarkovPresetsData
import com.example.data.TarkovRepository
import com.example.data.local.CustomPinEntity
import com.example.data.local.TarkovDatabase
import com.example.model.AmmoEntry
import com.example.model.KeyEntry
import com.example.model.LootCategory
import com.example.model.Rarity
import com.example.model.TarkovMap
import com.example.ui.map.TacticalMarker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import com.example.data.market.MarketItem
import com.example.data.market.MarketRepository
import com.example.data.market.MarketSyncState
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val iconName: String) {
    TACTICAL_MAP("3D Map", "map"),
    LIVE_MARKET("Flea Market", "trending_up"),
    KEYS_DIR("Keys & Locks", "vpn_key"),
    BALLISTICS("Ballistics", "grain")
}

class TarkovRadarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TarkovRepository
    private val marketRepository: MarketRepository

    init {
        val database = TarkovDatabase.getDatabase(application)
        repository = TarkovRepository(database.customPinDao())
        marketRepository = MarketRepository()
    }

    val maps: List<TarkovMap> = repository.getAllMaps()
    val ammoList: List<AmmoEntry> = repository.getAllAmmo()
    val keysList: List<KeyEntry> = repository.getAllKeys()

    // Live Flea Market state
    val marketItems: StateFlow<List<MarketItem>> = marketRepository.items
    val marketSyncState: StateFlow<MarketSyncState> = marketRepository.syncState

    fun refreshMarket() {
        marketRepository.refresh()
    }

    fun getMarketItem(name: String): MarketItem? {
        return marketRepository.findItemByFuzzyName(name)
    }

    // Navigation tab
    private val _currentTab = MutableStateFlow(AppTab.TACTICAL_MAP)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Active map (Default to Customs or Factory)
    private val _selectedMap = MutableStateFlow(maps.first())
    val selectedMap: StateFlow<TarkovMap> = _selectedMap.asStateFlow()

    // Floor selection: 0 is Ground floor by default so all primary exfils and loot are immediately visible
    private val _selectedFloorIndex = MutableStateFlow<Int?>(0)
    val selectedFloorIndex: StateFlow<Int?> = _selectedFloorIndex.asStateFlow()

    // 2D vs 3D Isometric Mode: Default to 2D Top-Down for ultimate clarity, toggleable to 3D Isometric
    private val _is3DMode = MutableStateFlow(false)
    val is3DMode: StateFlow<Boolean> = _is3DMode.asStateFlow()

    // Camera Pan & Zoom state
    private val _zoomScale = MutableStateFlow(1.25f)
    val zoomScale: StateFlow<Float> = _zoomScale.asStateFlow()

    private val _panOffset = MutableStateFlow(Offset.Zero)
    val panOffset: StateFlow<Offset> = _panOffset.asStateFlow()

    private val _showLabels = MutableStateFlow(true)
    val showLabels: StateFlow<Boolean> = _showLabels.asStateFlow()

    private val _highlightMarkerId = MutableStateFlow<String?>(null)
    val highlightMarkerId: StateFlow<String?> = _highlightMarkerId.asStateFlow()

    // Filters
    private val _selectedRarities = MutableStateFlow(Rarity.values().toSet())
    val selectedRarities: StateFlow<Set<Rarity>> = _selectedRarities.asStateFlow()

    private val _selectedCategory = MutableStateFlow<LootCategory?>(null)
    val selectedCategory: StateFlow<LootCategory?> = _selectedCategory.asStateFlow()

    private val _showExtracts = MutableStateFlow(true)
    val showExtracts: StateFlow<Boolean> = _showExtracts.asStateFlow()

    private val _showBosses = MutableStateFlow(true)
    val showBosses: StateFlow<Boolean> = _showBosses.asStateFlow()

    private val _showQuests = MutableStateFlow(true)
    val showQuests: StateFlow<Boolean> = _showQuests.asStateFlow()

    private val _showLockedDoors = MutableStateFlow(true)
    val showLockedDoors: StateFlow<Boolean> = _showLockedDoors.asStateFlow()

    private val _showContainers = MutableStateFlow(true)
    val showContainers: StateFlow<Boolean> = _showContainers.asStateFlow()

    private val _selectedContainerType = MutableStateFlow<String?>(null)
    val selectedContainerType: StateFlow<String?> = _selectedContainerType.asStateFlow()

    private val _showGrid = MutableStateFlow(true)
    val showGrid: StateFlow<Boolean> = _showGrid.asStateFlow()

    fun toggleLockedDoors() {
        _showLockedDoors.value = !_showLockedDoors.value
    }

    fun toggleContainers() {
        _showContainers.value = !_showContainers.value
    }

    fun selectContainerType(type: String?) {
        _selectedContainerType.value = type
    }

    // Map Overlay style & transparency
    private val _overlayMode = MutableStateFlow(com.example.model.MapOverlayMode.SATELLITE)
    val overlayMode: StateFlow<com.example.model.MapOverlayMode> = _overlayMode.asStateFlow()

    private val _satelliteOpacity = MutableStateFlow(0.92f)
    val satelliteOpacity: StateFlow<Float> = _satelliteOpacity.asStateFlow()

    fun setOverlayMode(mode: com.example.model.MapOverlayMode) {
        _overlayMode.value = mode
    }

    fun toggleOverlayMode() {
        _overlayMode.value = when (_overlayMode.value) {
            com.example.model.MapOverlayMode.SATELLITE -> com.example.model.MapOverlayMode.TACTICAL_HYBRID
            com.example.model.MapOverlayMode.TACTICAL_HYBRID -> com.example.model.MapOverlayMode.TOPO_SCHEMATIC
            com.example.model.MapOverlayMode.TOPO_SCHEMATIC -> com.example.model.MapOverlayMode.SATELLITE
        }
    }

    fun setSatelliteOpacity(opacity: Float) {
        _satelliteOpacity.value = opacity.coerceIn(0.2f, 1.0f)
    }

    // Active dialogs
    private val _selectedMarker = MutableStateFlow<TacticalMarker?>(null)
    val selectedMarker: StateFlow<TacticalMarker?> = _selectedMarker.asStateFlow()

    private val _showFullDetailDialog = MutableStateFlow(false)
    val showFullDetailDialog: StateFlow<Boolean> = _showFullDetailDialog.asStateFlow()

    private val _dropPinCoords = MutableStateFlow<Triple<Float, Float, Int>?>(null)
    val dropPinCoords: StateFlow<Triple<Float, Float, Int>?> = _dropPinCoords.asStateFlow()

    private val _showExtractChecklist = MutableStateFlow(false)
    val showExtractChecklist: StateFlow<Boolean> = _showExtractChecklist.asStateFlow()

    private val _showMapPicker = MutableStateFlow(false)
    val showMapPicker: StateFlow<Boolean> = _showMapPicker.asStateFlow()

    private val _showPoiDirectory = MutableStateFlow(false)
    val showPoiDirectory: StateFlow<Boolean> = _showPoiDirectory.asStateFlow()

    // Privacy & Security Disclosure Modal
    private val _showPrivacyPolicy = MutableStateFlow(false)
    val showPrivacyPolicy: StateFlow<Boolean> = _showPrivacyPolicy.asStateFlow()

    fun openPrivacyPolicy() { _showPrivacyPolicy.value = true }
    fun closePrivacyPolicy() { _showPrivacyPolicy.value = false }

    // Visited extracts in raid
    private val _visitedExtractIds = MutableStateFlow<Set<String>>(emptySet())
    val visitedExtractIds: StateFlow<Set<String>> = _visitedExtractIds.asStateFlow()

    // Raid Timer
    private val _raidRemainingSeconds = MutableStateFlow(maps.first().durationMinutes * 60)
    val raidRemainingSeconds: StateFlow<Int> = _raidRemainingSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var timerJob: Job? = null

    // Room DB custom pins for current map
    val customPins: StateFlow<List<CustomPinEntity>> = _selectedMap.flatMapLatest { map ->
        repository.getCustomPinsForMap(map.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun selectMap(map: TarkovMap) {
        _selectedMap.value = map
        _selectedFloorIndex.value = 0 // Ground floor by default
        _raidRemainingSeconds.value = map.durationMinutes * 60
        _isTimerRunning.value = false
        timerJob?.cancel()
        _visitedExtractIds.value = emptySet()
        resetCamera()
        _selectedMarker.value = null
        _highlightMarkerId.value = null
    }

    fun selectMapById(mapId: String) {
        val map = maps.find { it.id == mapId } ?: return
        selectMap(map)
    }

    fun selectFloor(floorIndex: Int?) {
        _selectedFloorIndex.value = floorIndex
    }

    fun toggle3DMode() {
        _is3DMode.value = !_is3DMode.value
    }

    fun toggleLabels() {
        _showLabels.value = !_showLabels.value
    }

    fun setZoomScale(scale: Float) {
        _zoomScale.value = scale.coerceIn(0.6f, 4.5f)
    }

    fun setPanOffset(offset: Offset) {
        _panOffset.value = offset
    }

    fun zoomIn() {
        _zoomScale.value = (_zoomScale.value * 1.3f).coerceAtMost(4.5f)
    }

    fun zoomOut() {
        _zoomScale.value = (_zoomScale.value / 1.3f).coerceAtLeast(0.6f)
    }

    fun resetCamera() {
        _zoomScale.value = 1.25f
        _panOffset.value = Offset.Zero
    }

    fun focusOnLocation(x: Float, y: Float, floorIndex: Int, marker: TacticalMarker) {
        _selectedFloorIndex.value = floorIndex
        _zoomScale.value = 2.0f

        // Center map around (x, y) where center is 500
        val targetNormX = (x - 500f) * 2.0f
        val targetNormY = (y - 500f) * 2.0f
        _panOffset.value = Offset(-targetNormX, -targetNormY)

        _selectedMarker.value = marker
        _highlightMarkerId.value = when (marker) {
            is TacticalMarker.Loot -> marker.spawn.id
            is TacticalMarker.Extract -> marker.extract.id
            is TacticalMarker.Boss -> marker.boss.name
            is TacticalMarker.Quest -> marker.quest.id
            is TacticalMarker.LockedDoor -> marker.lock.id
            is TacticalMarker.UserPin -> marker.pin.id.toString()
        }
    }

    fun toggleRarity(rarity: Rarity) {
        val current = _selectedRarities.value.toMutableSet()
        if (current.contains(rarity)) {
            if (current.size > 1) current.remove(rarity) else current.addAll(Rarity.values())
        } else {
            current.add(rarity)
        }
        _selectedRarities.value = current
    }

    fun resetRarities() {
        _selectedRarities.value = Rarity.values().toSet()
    }

    fun selectCategory(category: LootCategory?) {
        _selectedCategory.value = category
    }

    fun toggleExtracts() {
        _showExtracts.value = !_showExtracts.value
    }

    fun toggleBosses() {
        _showBosses.value = !_showBosses.value
    }

    fun toggleQuests() {
        _showQuests.value = !_showQuests.value
    }

    fun toggleGrid() {
        _showGrid.value = !_showGrid.value
    }

    fun selectMarker(marker: TacticalMarker) {
        _selectedMarker.value = marker
        _highlightMarkerId.value = when (marker) {
            is TacticalMarker.Loot -> marker.spawn.id
            is TacticalMarker.Extract -> marker.extract.id
            is TacticalMarker.Boss -> marker.boss.name
            is TacticalMarker.Quest -> marker.quest.id
            is TacticalMarker.LockedDoor -> marker.lock.id
            is TacticalMarker.UserPin -> marker.pin.id.toString()
        }
    }

    fun openFullDetail() {
        _showFullDetailDialog.value = true
    }

    fun dismissFullDetail() {
        _showFullDetailDialog.value = false
    }

    fun dismissMarkerDetail() {
        _selectedMarker.value = null
        _highlightMarkerId.value = null
    }

    fun openPoiDirectory() {
        _showPoiDirectory.value = true
    }

    fun dismissPoiDirectory() {
        _showPoiDirectory.value = false
    }

    fun initiateDropPin(x: Float, y: Float, floorIndex: Int) {
        _dropPinCoords.value = Triple(x, y, floorIndex)
    }

    fun dismissDropPin() {
        _dropPinCoords.value = null
    }

    fun saveCustomPin(pin: CustomPinEntity) {
        viewModelScope.launch {
            repository.addCustomPin(pin)
            _dropPinCoords.value = null
        }
    }

    fun deletePin(pinId: Long) {
        viewModelScope.launch {
            repository.removePinById(pinId)
            _selectedMarker.value = null
            _highlightMarkerId.value = null
        }
    }

    fun openExtractChecklist() {
        _showExtractChecklist.value = true
    }

    fun dismissExtractChecklist() {
        _showExtractChecklist.value = false
    }

    fun openMapPicker() {
        _showMapPicker.value = true
    }

    fun dismissMapPicker() {
        _showMapPicker.value = false
    }

    fun toggleExtractVisited(extractId: String) {
        val current = _visitedExtractIds.value.toMutableSet()
        if (current.contains(extractId)) {
            current.remove(extractId)
        } else {
            current.add(extractId)
        }
        _visitedExtractIds.value = current
    }

    fun toggleRaidTimer() {
        if (_isTimerRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isTimerRunning.value = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value && _raidRemainingSeconds.value > 0) {
                delay(1000)
                _raidRemainingSeconds.value = (_raidRemainingSeconds.value - 1).coerceAtLeast(0)
            }
            if (_raidRemainingSeconds.value == 0) {
                _isTimerRunning.value = false
            }
        }
    }

    private fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetRaidTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        _raidRemainingSeconds.value = _selectedMap.value.durationMinutes * 60
    }
}
