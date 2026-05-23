package com.example.minlishlite.presentation.deck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minlishlite.presentation.component.AppButton
import com.example.minlishlite.presentation.component.AppDestructiveButton
import com.example.minlishlite.presentation.component.AppOutlinedButton
import com.example.minlishlite.presentation.component.AppTextField
import com.example.minlishlite.ui.theme.Background
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted

@Composable
fun AddEditDeckScreen(
    deckId: Int?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditDeckViewModel = viewModel(factory = AddEditDeckViewModel.provideFactory(deckId))
) {
    val state by viewModel.uiState.collectAsState()
    val isEdit = deckId != null
    val scrollState = rememberScrollState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Screen Title
        Text(
            text = if (isEdit) "Chỉnh sửa bộ từ" else "Tạo bộ từ vựng mới",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurface,
            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
        )

        Text(
            text = if (isEdit) "Cập nhật tên, mô tả hoặc phân loại cho bộ từ vựng của bạn."
                   else "Bộ từ vựng giúp bạn phân loại từ theo chủ đề, học và theo dõi tốt hơn.",
            fontSize = 14.sp,
            color = OnSurfaceMuted,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Inputs
        AppTextField(
            value = state.name,
            onValueChange = { viewModel.onNameChange(it) },
            label = "Tên bộ từ *",
            placeholder = "Ví dụ: Từ vựng IELTS, Giao tiếp hàng ngày",
            errorText = state.nameError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        AppTextField(
            value = state.description,
            onValueChange = { viewModel.onDescriptionChange(it) },
            label = "Mô tả bộ từ",
            placeholder = "Ghi chú ngắn về mục đích của bộ từ",
            singleLine = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        AppTextField(
            value = state.tag,
            onValueChange = { viewModel.onTagChange(it) },
            label = "Nhãn phân loại (Tag)",
            placeholder = "Ví dụ: IELTS, TOEIC, TRAVEL",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Save & Back Buttons
        AppButton(
            text = if (isEdit) "Lưu thay đổi" else "Tạo bộ từ",
            onClick = { viewModel.onSaveDeck(onBackClick) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.name.isNotBlank() && !state.isSaving
        )

        Spacer(modifier = Modifier.height(12.dp))

        AppOutlinedButton(
            text = "Hủy bỏ",
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        )

        if (isEdit) {
            Spacer(modifier = Modifier.height(24.dp))
            
            AppDestructiveButton(
                text = "Xóa bộ từ vựng này",
                onClick = { showDeleteConfirmation = true },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            confirmButton = {
                AppButton(
                    text = "Xóa bộ từ",
                    onClick = {
                        viewModel.onDeleteDeck(onBackClick)
                        showDeleteConfirmation = false
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
            },
            dismissButton = {
                AppOutlinedButton(
                    text = "Hủy",
                    onClick = { showDeleteConfirmation = false },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
            },
            title = {
                Text(
                    text = "Xóa bộ từ vựng này?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            },
            text = {
                Text(
                    text = "Hành động này không thể hoàn tác. Toàn bộ từ vựng và lịch sử ôn tập thuộc bộ từ này sẽ bị xóa vĩnh viễn.",
                    fontSize = 14.sp,
                    color = OnSurfaceMuted
                )
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditDeckScreenPreview() {
    MinLishLiteTheme {
        AddEditDeckScreen(
            deckId = null,
            onBackClick = {}
        )
    }
}
