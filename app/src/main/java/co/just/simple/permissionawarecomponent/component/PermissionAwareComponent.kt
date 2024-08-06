package co.just.simple.permissionawarecomponent.component

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionAwareComponent(
    modifier: Modifier = Modifier,
    permissions: List<String>,
    clickEvent: Int?,
    onPermissionGranted: () -> Unit,
    shouldShowSetting: () -> Unit,
    content: @Composable() (BoxScope.() -> Unit),
) {
    var requestedTime by rememberSaveable { mutableStateOf(0L) }
    var onResultedTime by rememberSaveable { mutableStateOf(0L) }
    val deniedPermissions = rememberSaveable { mutableStateOf(listOf<String>()) }
    val shouldShowRationale = remember { mutableStateOf(false) }
    val multiplePermissionState = rememberMultiplePermissionsState(
        permissions = permissions,
        onPermissionsResult = { result ->
            deniedPermissions.value = emptyList()
            val list = mutableListOf<String>()
            result.entries.forEach { entry ->
                if (!entry.value) list.add(entry.key)
            }
            deniedPermissions.value = list
            onResultedTime = System.currentTimeMillis()
            val diff = onResultedTime - requestedTime
            if(diff < 1500) {
                shouldShowRationale.value = true
                requestedTime = 0L
                onResultedTime = 0L
            }
        }
    )
    LaunchedEffect(shouldShowRationale.value) {
        if(shouldShowRationale.value) {
            if(deniedPermissions.value.isNotEmpty()) {
                if (!multiplePermissionState.allPermissionsGranted){
                    if (multiplePermissionState.shouldShowRationale) {
                        multiplePermissionState.launchMultiplePermissionRequest()
                    } else {
                        // show setting dialog
                        shouldShowSetting()
                    }
                } else {
                    onPermissionGranted()
                    deniedPermissions.value = emptyList()
                }
            }
            shouldShowRationale.value = false
        }
    }
    LaunchedEffect(key1 = clickEvent) {
        clickEvent?.let {
            if(deniedPermissions.value.isNotEmpty()) {
                if (!multiplePermissionState.allPermissionsGranted){
                    if (multiplePermissionState.shouldShowRationale) {
                        multiplePermissionState.launchMultiplePermissionRequest()
                    } else {
                        // show setting dialog
                        shouldShowSetting()
                    }
                } else {
                    onPermissionGranted()
                    deniedPermissions.value = emptyList()
                }
            } else {
                if (!multiplePermissionState.allPermissionsGranted) {
                    requestedTime = System.currentTimeMillis()
                    multiplePermissionState.launchMultiplePermissionRequest()
                }
            }
        }
    }
    Box(modifier = modifier, content = {
        content()
    })
}


fun Context.checkPermissionsDenied(permissions : List<String>) : Boolean {
    val needsPermission = arrayListOf<String>()
    for (perms in permissions) {
        if (ContextCompat.checkSelfPermission(
                this,
                perms
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            needsPermission.add(perms)
        }
    }
    return needsPermission.isNotEmpty()
}

fun Context.openAppSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts("package", packageName, null)
        }
    startActivity(intent)
}