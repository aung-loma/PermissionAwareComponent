#  For Permission request

## 1) Create permission string list

```kotlin
   val list =  listOf(
   Manifest.permission.CAMERA,
   Manifest.permission.READ_MEDIA_IMAGES,
   Manifest.permission.READ_MEDIA_VIDEO,
   Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
   )
```
<br><br>

## 2) Let go to create PermissionAwareComponent

```kotlin
@Composable
fun Screen(){
    PermissionAwareComponent(
        permissions = list, // permission list
        onPermissionGranted = {
            Log.d("Permission", "Call back Permission granted")
            // pick media or something
            // pickPhotoOrVideo.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        },
        shouldShowSetting = {
            Log.d("Permission", "Call back Permission denied")
            // can show go to setting message
        }
    ) { event ->
        Button(onClick = {
           // for click event aware , need to pass true
            event.value = true
        }) {
            Text(text = "Click me")
        }
    }
}
```