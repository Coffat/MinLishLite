package com.example.minlishlite.presentation.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minlishlite.presentation.component.AppButton
import com.example.minlishlite.presentation.component.AppOutlinedButton
import com.example.minlishlite.presentation.component.AppTextField
import com.example.minlishlite.presentation.component.LoadingState
import com.example.minlishlite.ui.theme.BorderColor
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.Primary
import com.example.minlishlite.ui.theme.PrimarySoft
import com.example.minlishlite.ui.theme.Surface

@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = viewModel(factory = OnboardingViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsState()

    // Redirect to Home if user profile already exists
    LaunchedEffect(state.userExists) {
        if (state.userExists == true) {
            onNavigateToHome()
        }
    }

    if (state.userExists == null) {
        LoadingState(modifier = modifier)
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (!state.showSetupForm) {
                // Welcome / Benefits Slide
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "MinLish Lite",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Học từ vựng thông minh mỗi ngày",
                        fontSize = 16.sp,
                        color = OnSurfaceMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    BenefitCard(
                        title = "Thẻ ghi nhớ (Flashcards)",
                        description = "Ghi nhớ từ vựng dễ dàng với giao diện thẻ lật trực quan."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BenefitCard(
                        title = "Lặp lại ngắt quãng (SRS)",
                        description = "Tự động lập lịch ôn tập dựa trên mức độ ghi nhớ của bạn."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BenefitCard(
                        title = "Theo dõi tiến độ",
                        description = "Theo dõi số lượng từ đã học và lịch sử ôn tập mỗi ngày."
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    AppButton(
                        text = "Bắt đầu thiết lập",
                        onClick = { viewModel.onGetStartedClick() },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AppOutlinedButton(
                        text = "Tiếp tục với khách (Guest)",
                        onClick = { viewModel.onContinueAsGuest(onNavigateToHome) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isSaving
                    )
                }
            } else {
                // Profile Setup Form Slide
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Tạo hồ sơ của bạn",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Vui lòng cung cấp một số thông tin cơ bản để bắt đầu hành trình học tập.",
                        fontSize = 14.sp,
                        color = OnSurfaceMuted
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    AppTextField(
                        value = state.name,
                        onValueChange = { viewModel.onNameChange(it) },
                        label = "Họ và tên *",
                        placeholder = "Nhập tên của bạn",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AppTextField(
                        value = state.goal,
                        onValueChange = { viewModel.onGoalChange(it) },
                        label = "Mục tiêu học tập *",
                        placeholder = "Ví dụ: Học 10 từ mỗi ngày",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Trình độ tiếng Anh",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val levels = listOf("Beginner", "Intermediate", "Advanced")
                        levels.forEach { level ->
                            val isSelected = state.level == level
                            val containerColor = if (isSelected) PrimarySoft else Surface
                            val contentColor = if (isSelected) Primary else OnSurfaceMuted
                            val borderColor = if (isSelected) Primary else BorderColor

                            Card(
                                onClick = { viewModel.onLevelChange(level) },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, borderColor),
                                colors = CardDefaults.cardColors(
                                    containerColor = containerColor,
                                    contentColor = contentColor
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = level,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                AppButton(
                    text = "Hoàn tất & Bắt đầu",
                    onClick = { viewModel.onSaveProfile(onNavigateToHome) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    enabled = state.name.isNotBlank() && state.goal.isNotBlank() && !state.isSaving
                )
            }
        }
    }
}

@Composable
fun BenefitCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderColor),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = OnSurfaceMuted,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    MinLishLiteTheme {
        OnboardingScreen(onNavigateToHome = {})
    }
}
