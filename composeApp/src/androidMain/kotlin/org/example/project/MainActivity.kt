package org.example.project

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import java.io.InputStream
import java.util.Base64
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : ComponentActivity() {

    private var continuation:
            kotlin.coroutines.Continuation<PickedImage?>? = null

    private val pickMedia =
        registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->

            if (uri == null) {
                continuation?.resume(null)
                return@registerForActivityResult
            }

            val result = uriToPickedImage(uri)

            continuation?.resume(result)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImagePickerBridge.pickImage = {
            suspendCoroutine { cont ->
                continuation = cont

                pickMedia.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }

        setContent {
            App()
        }
    }

    private fun uriToPickedImage(uri: Uri): PickedImage? {
        return try {

            val inputStream: InputStream? =
                contentResolver.openInputStream(uri)

            val bytes = inputStream?.readBytes() ?: return null

            val base64 =
                Base64.getEncoder().encodeToString(bytes)

            val dataUrl = "data:image/jpeg;base64,$base64"

            PickedImage(
                dataUrl = dataUrl,
                fileName = "image.jpg"
            )

        } catch (e: Exception) {
            null
        }
    }
}