package org.example.project

object ImagePickerBridge {
    var pickImage:
            (suspend () -> PickedImage?)? = null
}