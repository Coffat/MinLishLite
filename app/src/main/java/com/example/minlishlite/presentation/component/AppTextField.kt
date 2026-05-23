package com.example.minlishlite.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minlishlite.ui.theme.BorderColor
import com.example.minlishlite.ui.theme.ErrorColor
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.Primary
import com.example.minlishlite.ui.theme.Surface

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null
) {
    val isError = errorText != null

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isError) ErrorColor else OnSurface,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(if (singleLine) 56.dp else 120.dp),
            enabled = enabled,
            singleLine = singleLine,
            placeholder = placeholder?.let {
                {
                    Text(
                        text = it,
                        color = OnSurfaceMuted,
                        fontSize = 16.sp
                    )
                }
            },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (isError) ErrorColor else OnSurfaceMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingIcon = when {
                isError -> {
                    {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = ErrorColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                trailingIcon != null -> {
                    {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = OnSurfaceMuted,
                            modifier = Modifier
                                .size(20.dp)
                                .then(
                                    if (onTrailingIconClick != null) {
                                        Modifier.clickable(onClick = onTrailingIconClick)
                                    } else {
                                        Modifier
                                    }
                                )
                        )
                    }
                }
                else -> null
            },
            isError = isError,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = OnSurface,
                unfocusedTextColor = OnSurface,
                disabledTextColor = OnSurfaceMuted,
                focusedContainerColor = Surface,
                unfocusedContainerColor = Surface,
                disabledContainerColor = Color(0xFFF1F5F9),
                focusedBorderColor = Primary,
                unfocusedBorderColor = BorderColor,
                disabledBorderColor = BorderColor,
                errorBorderColor = ErrorColor,
                errorLabelColor = ErrorColor
            )
        )

        if (errorText != null) {
            Text(
                text = errorText,
                color = ErrorColor,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppTextFieldPreview() {
    MinLishLiteTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            AppTextField(
                value = "",
                onValueChange = {},
                label = "Username *",
                placeholder = "Enter your username",
                leadingIcon = Icons.Default.Info
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppTextField(
                value = "invalid_user",
                onValueChange = {},
                label = "Email Address",
                errorText = "Invalid email format"
            )
        }
    }
}
