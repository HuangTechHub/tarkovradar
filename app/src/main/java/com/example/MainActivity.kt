package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ballistics.BallisticsScreen
import com.example.ui.keys.KeysDirectoryScreen
import com.example.ui.map.TacticalMapScreen
import com.example.ui.market.LiveMarketScreen
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.TarkovBackground
import com.example.ui.theme.TarkovRadarTheme
import com.example.ui.theme.TarkovSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.viewmodel.AppTab
import com.example.viewmodel.TarkovRadarViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TarkovRadarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TarkovRadarTheme {
                TarkovRadarApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TarkovRadarApp(viewModel: TarkovRadarViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val marketItems by viewModel.marketItems.collectAsState()
    val marketSyncState by viewModel.marketSyncState.collectAsState()

    // Predictive back handler: return to Map when on sub-screens
    BackHandler(enabled = currentTab != AppTab.TACTICAL_MAP) {
        viewModel.selectTab(AppTab.TACTICAL_MAP)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(TarkovBackground)
            .statusBarsPadding()
            .testTag("tarkov_radar_scaffold"),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("tarkov_bottom_navigation"),
                containerColor = TarkovSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.TACTICAL_MAP,
                    onClick = { viewModel.selectTab(AppTab.TACTICAL_MAP) },
                    icon = { Icon(Icons.Default.Map, contentDescription = "3D Tactical Map") },
                    label = {
                        Text(
                            text = "3D MAP",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (currentTab == AppTab.TACTICAL_MAP) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TarkovSurface,
                        selectedTextColor = TacticalAmber,
                        indicatorColor = TacticalAmber,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_map")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.LIVE_MARKET,
                    onClick = { viewModel.selectTab(AppTab.LIVE_MARKET) },
                    icon = { Icon(Icons.Default.TrendingUp, contentDescription = "Live Flea Market") },
                    label = {
                        Text(
                            text = "FLEA INTEL",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (currentTab == AppTab.LIVE_MARKET) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TarkovSurface,
                        selectedTextColor = TacticalEmerald,
                        indicatorColor = TacticalEmerald,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_market")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.KEYS_DIR,
                    onClick = { viewModel.selectTab(AppTab.KEYS_DIR) },
                    icon = { Icon(Icons.Default.VpnKey, contentDescription = "Keys & Locks") },
                    label = {
                        Text(
                            text = "KEYS",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (currentTab == AppTab.KEYS_DIR) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TarkovSurface,
                        selectedTextColor = TacticalAmber,
                        indicatorColor = TacticalAmber,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_keys")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.BALLISTICS,
                    onClick = { viewModel.selectTab(AppTab.BALLISTICS) },
                    icon = { Icon(Icons.Default.Grain, contentDescription = "Ballistics Matrix") },
                    label = {
                        Text(
                            text = "BALLISTICS",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (currentTab == AppTab.BALLISTICS) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TarkovSurface,
                        selectedTextColor = TacticalCyan,
                        indicatorColor = TacticalCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_ballistics")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.TACTICAL_MAP -> TacticalMapScreen(viewModel = viewModel)
                AppTab.LIVE_MARKET -> LiveMarketScreen(
                    items = marketItems,
                    syncState = marketSyncState,
                    onRefresh = { viewModel.refreshMarket() }
                )
                AppTab.KEYS_DIR -> KeysDirectoryScreen(
                    keysList = viewModel.keysList,
                    getMarketItem = { viewModel.getMarketItem(it) },
                    onSelectKeyMap = { mapId ->
                        viewModel.selectMapById(mapId)
                        viewModel.selectTab(AppTab.TACTICAL_MAP)
                    }
                )
                AppTab.BALLISTICS -> BallisticsScreen(ammoList = viewModel.ammoList)
            }
        }
    }
}
