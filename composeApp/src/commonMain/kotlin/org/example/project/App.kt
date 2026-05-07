package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    MaterialTheme {

        var name by remember { mutableStateOf("") }
        var bio by remember { mutableStateOf("") }
        var github by remember { mutableStateOf("") }
        var zenn by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("ポートフォリオ自動生成", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(16.dp))

            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("名前") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            TextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("自己紹介") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            TextField(
                value = github,
                onValueChange = { github = it },
                label = { Text("GitHub URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            TextField(
                value = zenn,
                onValueChange = { zenn = it },
                label = { Text("Zenn URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(onClick = {
                CoroutineScope(Dispatchers.Default).launch {
                    val response = ApiClient.client.post("http://0.0.0.0:8080/profile") {
                        contentType(ContentType.Application.Json)
                        setBody(
                            ProfileRequest(
                                name = name,
                                bio = bio,
                                github = github,
                                zenn = zenn
                            )
                        )
                    }
                    println(response.bodyAsText())
                }
            }) {
                Text("ポートフォリオ生成") // ← 抜けていた
            }
        }
    }
}