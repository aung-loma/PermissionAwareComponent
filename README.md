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

## 2) Need to create click event 

```kotlin
    var clickEvent by remember { mutableStateOf<Int?>(null) }
```

## 3) Let go to create PermissionAwareComponent

```kotlin
@Composable
fun Screen(){
    PermissionAwareComponent(
        permissions = list, // permission list
        clickEvent = clickEvent, // click event 
        onPermissionGranted = {
            Log.d("Permission", "Call back Permission granted")
            // pick media or something
            // pickPhotoOrVideo.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        },
        shouldShowSetting = {
            Log.d("Permission", "Call back Permission denied")
            // can show go to setting message
        }
    ) {
        Button(onClick = {
           // for click event aware 
           // clickEvent = Random.nextInt(1, 100)
        }) {
            Text(text = "Click me")
        }
    }
}
```