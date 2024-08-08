package co.just.simple.permissionawarecomponent

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import co.just.simple.permissionawarecomponent.component.MultiActionDialog
import co.just.simple.permissionawarecomponent.component.PermissionAwareComponent
import co.just.simple.permissionawarecomponent.component.checkPermissionsDenied
import co.just.simple.permissionawarecomponent.component.openAppSetting
import co.just.simple.permissionawarecomponent.ui.theme.PermissionAwareComponentTheme

class MainActivity : ComponentActivity() {
    val PERMISSIONS = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
            )
        }

        Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU -> {
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
            )
        }

        else -> {
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PermissionAwareComponentTheme {
                val context = LocalContext.current
                val pickPhotoOrVideo = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                        uri?.let { chooseUri ->
                            Toast.makeText(this,"choose uri: $chooseUri",Toast.LENGTH_SHORT).show()
                        }
                    }
                var showMultiDialog by remember { mutableStateOf(false) }
                if(showMultiDialog) {
                    MultiActionDialog(
                        title = "Access Permission for choosing photos and videos",
                        message = "Choose photos or videos. You can change preferences at any time in your device settings.",
                        onConfirm = {
                            showMultiDialog = false
                            context.openAppSetting()
                        },
                        onDismiss = {
                            showMultiDialog = false
                        })
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    PermissionAwareComponent(
                        permissions = PERMISSIONS,
                        onPermissionGranted = {
                            Log.d("Permission", "Call back Permission granted")
                            pickPhotoOrVideo.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                        },
                        shouldShowSetting = {
                            Log.d("Permission", "Call back Permission denied")
                            showMultiDialog = true
                        },
                        content = { event ->
                            Button(onClick = {
                                if(context.checkPermissionsDenied(PERMISSIONS)) {
                                    event.value = true
                                } else {
                                    Log.d("Permission", "Permission granted")
                                    pickPhotoOrVideo.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                                }
                            }) {
                                Text(text = "Click me")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PermissionAwareComponentTheme {}
}