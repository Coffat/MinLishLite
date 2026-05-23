package com.example.minlishlite.presentation.settings

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minlishlite.presentation.component.AppButton
import com.example.minlishlite.presentation.component.AppOutlinedButton
import com.example.minlishlite.presentation.component.AppTextField
import com.example.minlishlite.ui.theme.Background
import com.example.minlishlite.ui.theme.BorderColor
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.Primary
import com.example.minlishlite.ui.theme.PrimarySoft
import com.example.minlishlite.ui.theme.Surface
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.onReminderEnabledChange(true)
        }
    }

    val onReminderToggle: (Boolean) -> Unit = { enabled ->
        if (!enabled) {
            viewModel.onReminderEnabledChange(false)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (granted) {
                viewModel.onReminderEnabledChange(true)
            } else {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            viewModel.onReminderEnabledChange(true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Cài đặt hệ thống",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurface
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Profile Section
        Text(
            text = "HỒ SƠ CÁ NHÂN",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurfaceMuted,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderColor),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.user?.name ?: "Guest User",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mục tiêu: ${state.user?.goal ?: "Chưa thiết lập"}",
                        fontSize = 14.sp,
                        color = OnSurfaceMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // Level badge
                    Box(
                        modifier = Modifier
                            .background(PrimarySoft, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Trình độ: ${state.user?.level ?: "Beginner"}",
                            color = Primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.onShowEditProfile(true) },
                    modifier = Modifier
                        .background(PrimarySoft, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = Primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Study Goals Section
        Text(
            text = "MỤC TIÊU HỌC TẬP",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurfaceMuted,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderColor),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Số từ mới mỗi ngày",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface
                    )
                    Text(
                        text = "${state.newWordsPerDay} từ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = state.newWordsPerDay.toFloat(),
                    onValueChange = { viewModel.onNewWordsPerDayChange(it.toInt()) },
                    valueRange = 5f..50f,
                    colors = SliderDefaults.colors(
                        activeTrackColor = Primary,
                        inactiveTrackColor = BorderColor,
                        thumbColor = Primary
                    )
                )

                Text(
                    text = "Home sẽ gợi ý tối đa ${state.newWordsPerDay} từ mới chưa học mỗi ngày.",
                    fontSize = 12.sp,
                    color = OnSurfaceMuted,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Notification Settings Section
        Text(
            text = "THÔNG BÁO & NHẮC NHỞ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurfaceMuted,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderColor),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = OnSurfaceMuted
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Nhắc nhở học tập hàng ngày",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface
                            )
                            Text(
                                text = "Gửi thông báo nhắc ôn tập từ vựng",
                                fontSize = 12.sp,
                                color = OnSurfaceMuted
                            )
                        }
                    }

                    Switch(
                        checked = state.reminderEnabled,
                        onCheckedChange = onReminderToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Primary,
                            uncheckedThumbColor = Color.LightGray,
                            uncheckedTrackColor = BorderColor
                        )
                    )
                }

                if (state.reminderEnabled) {
                    Divider(color = BorderColor, thickness = 1.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Thời gian nhắc nhở",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface
                            )
                            Text(
                                text = "Thông báo được gửi vào khung giờ này",
                                fontSize = 12.sp,
                                color = OnSurfaceMuted
                            )
                        }

                        TextButton(
                            onClick = {
                                val parts = state.reminderTime.split(":")
                                val hour = parts.getOrNull(0)?.toIntOrNull() ?: 9
                                val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

                                TimePickerDialog(
                                    context,
                                    { _, selectedHour, selectedMinute ->
                                        val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                                        viewModel.onReminderTimeChange(formattedTime)
                                    },
                                    hour,
                                    minute,
                                    true
                                ).show()
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = Primary)
                        ) {
                            Text(
                                text = state.reminderTime,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // Edit Profile Dialog
    if (state.showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onShowEditProfile(false) },
            confirmButton = {
                AppButton(
                    text = "Lưu thay đổi",
                    onClick = { viewModel.onSaveProfile() },
                    enabled = state.editName.isNotBlank() && state.editGoal.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
            },
            dismissButton = {
                AppOutlinedButton(
                    text = "Hủy",
                    onClick = { viewModel.onShowEditProfile(false) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
            },
            title = {
                Text(
                    text = "Chỉnh sửa hồ sơ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AppTextField(
                        value = state.editName,
                        onValueChange = { viewModel.onEditNameChange(it) },
                        label = "Họ và tên *",
                        placeholder = "Tên hiển thị của bạn",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = state.editGoal,
                        onValueChange = { viewModel.onEditGoalChange(it) },
                        label = "Mục tiêu học tập *",
                        placeholder = "Ví dụ: Học 10 từ mỗi ngày",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Trình độ tiếng Anh",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val levels = listOf("Beginner", "Intermediate", "Advanced")
                        levels.forEach { level ->
                            val isSelected = state.editLevel == level
                            val containerColor = if (isSelected) PrimarySoft else Surface
                            val contentColor = if (isSelected) Primary else OnSurfaceMuted
                            val borderColor = if (isSelected) Primary else BorderColor

                            Card(
                                onClick = { viewModel.onEditLevelChange(level) },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, borderColor),
                                colors = CardDefaults.cardColors(
                                    containerColor = containerColor,
                                    contentColor = contentColor
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = level,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MinLishLiteTheme {
        SettingsScreen()
    }
}
