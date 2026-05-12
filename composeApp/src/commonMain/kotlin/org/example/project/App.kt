package org.example.project

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch

// カラーテーマ
val PrimaryBlue = Color(0xFF3B82F6)
val DarkBg = Color(0xFF0F172A)
val CardWhite = Color(0xFFFFFFFF)
val SubtleBorder = Color(0xFFE2E8F0)

// スキルのマスターデータ
val SkillSuggestions = listOf(
    "Kotlin", "Swift", "Java", "JavaScript", "TypeScript", "Python", "Go", "Rust",
    "Compose", "React", "Vue.js", "Next.js", "Flutter", "Spring Boot", "Ktor",
    "Firebase", "AWS", "Google Cloud", "Docker", "Kubernetes", "Git", "SQL",
    "機械学習", "データ分析", "デザイン", "プロジェクト管理"
).sorted()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // ステート管理
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var iconUrl by remember { mutableStateOf("") }
    var iconFileName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var projects by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var github by remember { mutableStateOf("") }
    var zenn by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var generatedUrl by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // 必須項目のチェック
    val isFormValid = name.isNotBlank() && email.contains("@")

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("SwiftFolio", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {

                        // --- セクション1: 基本情報 ---
                        SectionHeader("基本情報")
                        CustomTextField(value = name, onValueChange = { name = it }, label = "氏名 *")
                        CustomTextField(value = role, onValueChange = { role = it }, label = "肩書き / 職種")

                        // アイコン選択
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable {
                                    scope.launch {
                                        val pickedImage = pickImageDataUrl()
                                        if (pickedImage != null) {
                                            iconUrl = pickedImage.dataUrl
                                            iconFileName = pickedImage.fileName
                                        }
                                    }
                                }
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = PrimaryBlue.copy(0.1f)) {
                                    Box(contentAlignment = Alignment.Center) { Text("📷", fontSize = 18.sp) }
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("プロフィール画像", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                    Text(if (iconFileName.isBlank()) "画像を選択してください" else iconFileName, fontSize = 12.sp, color = Color.Gray)
                                }
                                if (iconUrl.isNotBlank()) {
                                    Text("✕", Modifier.clickable { iconUrl = ""; iconFileName = "" }, color = Color.Gray, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // --- セクション2: 経歴・詳細 ---
                        SectionHeader("スキル・経歴")
                        CustomTextField(value = bio, onValueChange = { bio = it }, label = "自己紹介", singleLine = false)

                        // スキル選択（日本語ラベル）
                        SkillPickerField(currentSkills = skills, onSkillsChange = { skills = it })

                        CustomTextField(value = projects, onValueChange = { projects = it }, label = "代表的な制作物・実績", singleLine = false)
                        CustomTextField(value = experience, onValueChange = { experience = it }, label = "経験・強み", singleLine = false)

                        // --- セクション3: リンク・連絡先 ---
                        SectionHeader("ソーシャル / 連絡先")
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CustomTextField(value = github, onValueChange = { github = it }, label = "GitHub URL", modifier = Modifier.weight(1f))
                            CustomTextField(value = zenn, onValueChange = { zenn = it }, label = "Zenn URL", modifier = Modifier.weight(1f))
                        }
                        CustomTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "連絡先メールアドレス *",
                            imeAction = ImeAction.Done,
                            onDone = { focusManager.clearFocus() }
                        )

                        Spacer(Modifier.height(12.dp))

                        // --- 生成ボタン ---
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                scope.launch {
                                    isLoading = true
                                    generatedUrl = ""
                                    errorMessage = ""
                                    try {
                                        val httpResponse = ApiClient.client.post("http://10.0.2.2:8080/profile") {
                                            contentType(ContentType.Application.Json)
                                            setBody(ProfileRequest(
                                                name = name,
                                                role = role,
                                                iconUrl = iconUrl,
                                                bio = bio,
                                                skills = skills,
                                                projects = projects,
                                                experience = experience,
                                                github = github,
                                                zenn = zenn,
                                                email = email
                                            ))
                                        }

                                        if (!httpResponse.status.isSuccess()) {
                                            val body = httpResponse.bodyAsText()
                                            errorMessage = "APIエラー: ${httpResponse.status.value} $body"
                                        } else {
                                            val response = httpResponse.body<PortfolioResponse>()
                                            generatedUrl = response.url
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = e.message ?: "通信に失敗しました。サーバーが起動しているか確認してください。"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            enabled = !isLoading && isFormValid,
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("生成中...", fontWeight = FontWeight.Bold)
                            } else {
                                Text("ポートフォリオを公開する", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 生成結果表示（日本語）
                if (generatedUrl.isNotBlank()) {
                    SuccessCard(url = generatedUrl, context = context)
                }
                if (errorMessage.isNotBlank()) {
                    ErrorCard(message = errorMessage)
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
        Box(modifier = Modifier.size(4.dp, 16.dp).background(PrimaryBlue, RoundedCornerShape(2.dp)))
        Spacer(Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillPickerField(currentSkills: String, onSkillsChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = currentSkills,
            onValueChange = { onSkillsChange(it) },
            label = { Text("スキル (選択または入力)", fontSize = 13.sp) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = SubtleBorder,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            SkillSuggestions.forEach { skill ->
                DropdownMenuItem(
                    text = { Text(skill) },
                    onClick = {
                        val updated = if (currentSkills.isBlank()) skill
                        else if (currentSkills.contains(skill)) currentSkills
                        else "$currentSkills, $skill"
                        onSkillsChange(updated)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    onDone: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onDone?.invoke() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = SubtleBorder,
            focusedLabelColor = PrimaryBlue,
            unfocusedLabelColor = Color.Gray,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

@Composable
fun SuccessCard(url: String, context: android.content.Context) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7))) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("✨ ポートフォリオが完成しました！", fontWeight = FontWeight.Bold, color = Color(0xFF166534))
            Spacer(Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth().clickable {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                },
                shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = url, fontSize = 12.sp, color = PrimaryBlue, textDecoration = TextDecoration.Underline)
                    Spacer(Modifier.height(4.dp))
                    Text(text = "タップしてブラウザで開く", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ErrorCard(message: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))) {
        Text(text = message, color = Color(0xFF991B1B), modifier = Modifier.padding(16.dp), fontSize = 14.sp)
    }
}