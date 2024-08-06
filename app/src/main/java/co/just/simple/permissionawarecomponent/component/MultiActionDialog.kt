package co.just.simple.permissionawarecomponent.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun MultiActionDialog(
    title: String,
    message: String,
    btnPositiveText: String = "Yes",
    btnNegativeText: String = "No",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss.invoke() },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.W400,
                    color = MaterialTheme.colorScheme.surfaceTint,
                    fontSize = 16.sp
                ),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.W400,
                    color = MaterialTheme.colorScheme.surfaceTint,
                    fontSize = 14.sp
                ),
            )
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss.invoke()
                }
            ) {
                Text(
                    btnNegativeText,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.W500,
                        color = Color.Gray,
                        fontSize = 14.sp
                    ),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm.invoke()
                }
            ) {
                Text(
                    text = btnPositiveText,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.W500,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    ),
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    )
}

@Preview
@Composable
fun MultiActionDialogPreview() {
    MultiActionDialog(
        title = "Alert",
        message = "Are you sure you want to see this dialog ?",
        onDismiss = {},
        onConfirm = {}
    )
}