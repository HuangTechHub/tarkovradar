package com.example.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.CustomPinEntity
import com.example.ui.components.TacticalChip
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TarkovSurface
import com.example.ui.theme.TarkovSurfaceBorder
import com.example.ui.theme.TarkovSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DropPinDialog(
    mapId: String,
    x: Float,
    y: Float,
    floorIndex: Int,
    onConfirm: (CustomPinEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("AIRDROP") }

    val tags = listOf("AIRDROP", "DEAD_BODY", "STASH", "SNIPER", "DANGER")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(BorderStroke(1.5.dp, TacticalCyan), RoundedCornerShape(12.dp))
                .testTag("drop_pin_dialog"),
            color = TarkovSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "DROP TACTICAL WAYPOINT",
                    color = TacticalCyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Text(
                    text = "Coordinates: ${x.toInt()}, ${y.toInt()} (Floor $floorIndex)",
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Waypoint Label (e.g. PMC Body / Airdrop)") },
                    modifier = Modifier.fillMaxWidth().testTag("pin_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TacticalCyan,
                        unfocusedBorderColor = TarkovSurfaceBorder
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Tactical Note / Loot Details") },
                    modifier = Modifier.fillMaxWidth().testTag("pin_note_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TacticalCyan,
                        unfocusedBorderColor = TarkovSurfaceBorder
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Waypoint Type:", color = TextSecondary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tags.take(3).forEach { tag ->
                        TacticalChip(
                            text = tag,
                            isSelected = selectedTag == tag,
                            onClick = { selectedTag = tag },
                            activeColor = TacticalCyan,
                            testTag = "tag_chip_$tag"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tags.drop(3).forEach { tag ->
                        TacticalChip(
                            text = tag,
                            isSelected = selectedTag == tag,
                            onClick = { selectedTag = tag },
                            activeColor = TacticalCyan,
                            testTag = "tag_chip_$tag"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).testTag("cancel_pin_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = TarkovSurfaceElevated),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("CANCEL", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            val pinTitle = title.ifBlank { selectedTag }
                            onConfirm(
                                CustomPinEntity(
                                    mapId = mapId,
                                    title = pinTitle,
                                    note = note,
                                    x = x,
                                    y = y,
                                    floorIndex = floorIndex,
                                    tagType = selectedTag
                                )
                            )
                        },
                        modifier = Modifier.weight(1f).testTag("save_pin_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = TacticalCyan),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("SET PIN", color = TarkovSurface, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
