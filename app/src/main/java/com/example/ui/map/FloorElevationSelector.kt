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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FloorLevel
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TarkovSurface
import com.example.ui.theme.TarkovSurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FloorElevationSelector(
    floors: List<FloorLevel>,
    selectedFloorIndex: Int?, // null means ALL floors 3D stacked
    floorPoiCounts: Map<Int, Int>, // floorIndex to count of loot & extracts
    onFloorSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF141C26).copy(alpha = 0.96f))
            .border(BorderStroke(1.2.dp, TacticalCyan.copy(alpha = 0.6f)), RoundedCornerShape(10.dp))
            .padding(5.dp)
            .testTag("floor_elevation_selector")
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "LEVELS",
                color = TacticalCyan,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )

            // "3D ALL" Button
            val isAllSelected = selectedFloorIndex == null
            Box(
                modifier = Modifier
                    .width(52.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isAllSelected) TacticalCyan else Color(0xFF1E293B))
                    .border(
                        BorderStroke(1.dp, if (isAllSelected) TacticalCyan else TarkovSurfaceBorder),
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { onFloorSelected(null) }
                    .padding(vertical = 6.dp)
                    .testTag("floor_button_all_3d"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "3D ALL",
                        color = if (isAllSelected) Color.Black else TacticalCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Floor levels from highest to lowest (e.g. 3F, 2F, 1F, G, B1, B2)
            val sortedFloors = floors.sortedByDescending { it.floorIndex }
            sortedFloors.forEach { floor ->
                val isSelected = selectedFloorIndex == floor.floorIndex
                val count = floorPoiCounts[floor.floorIndex] ?: 0

                Box(
                    modifier = Modifier
                        .width(52.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) TacticalAmber else Color(0xFF1E293B))
                        .border(
                            BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) TacticalAmber else TarkovSurfaceBorder
                            ),
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { onFloorSelected(floor.floorIndex) }
                        .padding(vertical = 5.dp)
                        .testTag("floor_button_${floor.codeName}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = floor.codeName,
                            color = if (isSelected) Color.Black else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = if (count > 0) "$count items" else "0 items",
                            color = if (isSelected) Color(0xFF451A03) else TextMuted,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
