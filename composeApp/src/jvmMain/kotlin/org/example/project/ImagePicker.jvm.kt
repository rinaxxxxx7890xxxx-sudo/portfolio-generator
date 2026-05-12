package org.example.project

import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.util.Base64
import java.util.Locale

actual suspend fun pickImageDataUrl(): PickedImage? {
    val dialog = FileDialog(null as Frame?, "アイコン画像を選択", FileDialog.LOAD).apply {
        filenameFilter = java.io.FilenameFilter { _, name ->
            val lowerName = name.lowercase(Locale.ROOT)
            lowerName.endsWith(".png") ||
                lowerName.endsWith(".jpg") ||
                lowerName.endsWith(".jpeg") ||
                lowerName.endsWith(".gif") ||
                lowerName.endsWith(".webp")
        }
        isVisible = true
    }

    val directory = dialog.directory ?: return null
    val fileName = dialog.file ?: return null
    val file = File(directory, fileName)
    val mimeType = file.mimeType()
    val base64 = Base64.getEncoder().encodeToString(file.readBytes())

    return PickedImage(
        dataUrl = "data:$mimeType;base64,$base64",
        fileName = file.name
    )
}

private fun File.mimeType(): String =
    when (extension.lowercase(Locale.ROOT)) {
        "jpg", "jpeg" -> "image/jpeg"
        "gif" -> "image/gif"
        "webp" -> "image/webp"
        else -> "image/png"
    }
