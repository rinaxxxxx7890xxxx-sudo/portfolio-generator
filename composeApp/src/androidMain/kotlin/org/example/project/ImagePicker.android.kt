package org.example.project

actual suspend fun pickImageDataUrl(): PickedImage? {
    return ImagePickerBridge.pickImage?.invoke()
}