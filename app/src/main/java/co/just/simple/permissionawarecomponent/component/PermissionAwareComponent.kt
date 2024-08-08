package co.just.simple.permissionawarecomponent.component

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
    onPermissionGranted: () -> Unit,
    shouldShowSetting: () -> Unit,
    content: @Composable() (BoxScope.(event: MutableState<Boolean>) -> Unit),
) {
    val event = rememberSaveable {  mutableStateOf(false)  }
    var requestedTime by rememberSaveable { mutableStateOf(0L) }
    var onResultedTime by rememberSaveable { mutableStateOf(0L) }
    val deniedPermissions = rememberSaveable { mutableStateOf(listOf<String>()) }
    val multiplePermissionState = rememberMultiplePermissionsState(
        permissions = permissions,
        onPermissionsResult = { result ->
            deniedPermissions.value = emptyList()
            val list = mutableListOf<String>()
            result.entries.forEach { entry ->
                if (!entry.value) list.add(entry.key)
            }
            deniedPermissions.value = list
            if(deniedPermissions.value.isNotEmpty()) {
                onResultedTime = System.currentTimeMillis()
                val diff = onResultedTime - requestedTime
                if(diff < 500) {
                    event.value = true
                    requestedTime = 0L
                    onResultedTime = 0L
                }
            } else {
                onPermissionGranted()
            }
        }
    )
    LaunchedEffect(key1 = event.value) {
        if(event.value) {
            if(deniedPermissions.value.isNotEmpty()) {
                if (!multiplePermissionState.allPermissionsGranted){
                    requestedTime = System.currentTimeMillis()
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
            event.value = false
        }
    }
    Box(modifier = modifier, content = {
        content(event)
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