package com.tomosensei.feature.onboarding.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tomosensei.core.designsystem.components.HankoSize
import com.tomosensei.core.designsystem.components.HankoStamp
import com.tomosensei.core.designsystem.components.Overline
import com.tomosensei.core.designsystem.components.WashiBackground
import com.tomosensei.core.designsystem.components.WashiCard
import com.tomosensei.core.designsystem.theme.HankoRed
import com.tomosensei.core.designsystem.theme.Manrope
import com.tomosensei.core.designsystem.theme.ShipporiMincho
import com.tomosensei.core.designsystem.theme.SuccessMoss
import com.tomosensei.core.designsystem.theme.SumiBlack
import com.tomosensei.core.designsystem.theme.SumiDark
import com.tomosensei.core.designsystem.theme.SumiLight
import com.tomosensei.core.designsystem.theme.SumiMid
import com.tomosensei.core.designsystem.theme.WashiCreamLight
import com.tomosensei.feature.onboarding.DeviceCheckResult
import com.tomosensei.feature.onboarding.OnboardingProgress
import com.tomosensei.feature.onboarding.OnboardingViewModel
import com.tomosensei.feature.onboarding.Preset

private const val TOTAL_STEPS = 11

@Composable
fun OnboardingFlow(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    WashiBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ProgressBar(step = state.step, total = TOTAL_STEPS)
            AnimatedContent(
                targetState = state.step,
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
                    (slideInHorizontally(tween(280)) { it * direction } + fadeIn(tween(220)))
                        .togetherWith(slideOutHorizontally(tween(280)) { -it * direction } + fadeOut(tween(180)))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = "onboarding-step",
            ) { step ->
                Box(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                    when (step) {
                        0 -> WelcomeStep()
                        1 -> DeviceCheckStep(state)
                        2 -> CommitmentStep()
                        3 -> PresetStep(state, viewModel::selectPreset)
                        4 -> PermissionStep()
                        5 -> GatedAppsStep(state, viewModel::toggleGatedApp)
                        6 -> PinStep(state, viewModel::setPin, viewModel::setPinConfirm)
                        7 -> ModelDownloadStep()
                        8 -> DailyGoalStep(state, viewModel::setDailyGoal)
                        9 -> SimulationStep()
                        10 -> DoneStep()
                    }
                }
            }
            BottomNavRow(
                state = state,
                onBack = viewModel::back,
                onNext = viewModel::next,
                onFinish = { viewModel.finish(onComplete) },
            )
        }
    }
}

@Composable
private fun ProgressBar(step: Int, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 56.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(total) { i ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(CircleShape)
                    .let {
                        if (i <= step) it.padding(0.dp) else it.padding(0.dp)
                    },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .let { mod ->
                            if (i <= step) mod else mod
                        },
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            color = if (i <= step) HankoRed else SumiLight.copy(alpha = 0.25f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
    ) {
        HankoStamp(text = "智", size = HankoSize.Lg)
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Selamat datang di\nTomo Sensei",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W700,
                fontSize = 32.sp,
                color = SumiBlack,
                lineHeight = 40.sp,
                letterSpacing = (-0.4).sp,
            ),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Cara baru belajar Jepang lewat friction. " +
                "Kebiasaan HP-mu akan jadi pemicu untuk recall.",
            style = TextStyle(
                fontFamily = Manrope,
                fontSize = 15.sp,
                color = SumiMid,
                lineHeight = 22.sp,
            ),
        )
    }
}

@Composable
private fun CommitmentStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Overline(text = "Komitmen")
        Spacer(Modifier.height(12.dp))
        Text(
            text = "App ini akan ganggu kamu — itu fiturnya.",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W600,
                fontSize = 26.sp,
                color = SumiBlack,
                lineHeight = 34.sp,
            ),
        )
        Spacer(Modifier.height(16.dp))
        BulletText("Setiap unlock HP, kartu kosakata akan muncul.")
        BulletText("Buka TikTok / IG — gate kecil di-trigger.")
        BulletText("Idle 20 menit, Sensei mengingatkan.")
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Kamu bisa atur intensitas kapan saja di tab Atur.",
            style = TextStyle(fontFamily = Manrope, fontSize = 13.sp, color = SumiLight),
        )
    }
}

@Composable
private fun BulletText(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(4.dp)
                .clip(CircleShape),
        ) {
            androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                drawCircle(color = HankoRed)
            }
        }
        Text(
            text = text,
            style = TextStyle(
                fontFamily = Manrope,
                fontSize = 14.sp,
                color = SumiDark,
                lineHeight = 20.sp,
            ),
        )
    }
}

@Composable
private fun PresetStep(state: OnboardingProgress, onSelect: (Preset) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Overline(text = "Pilih ritme")
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Seberapa keras kamu mau dipaksa?",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W600,
                fontSize = 24.sp,
                color = SumiBlack,
            ),
        )
        Spacer(Modifier.height(20.dp))
        Preset.entries.forEach { preset ->
            val selected = state.preset == preset
            WashiCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                tone = if (selected) WashiCreamLight else WashiCreamLight,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = preset.title,
                            style = TextStyle(
                                fontFamily = ShipporiMincho,
                                fontWeight = FontWeight.W600,
                                fontSize = 20.sp,
                                color = if (selected) HankoRed else SumiBlack,
                            ),
                        )
                        Text(
                            text = preset.tagline,
                            style = TextStyle(
                                fontFamily = ShipporiMincho,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontSize = 13.sp,
                                color = SumiMid,
                            ),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape),
                    ) {
                        androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                            drawCircle(
                                color = if (selected) HankoRed else SumiLight.copy(alpha = 0.3f),
                            )
                            if (selected) {
                                drawCircle(
                                    color = WashiCreamLight,
                                    radius = size.minDimension / 4f,
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(0.dp))
                TextButton(
                    onClick = { onSelect(preset) },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                ) { Text("", modifier = Modifier.fillMaxWidth().height(0.dp)) }
            }
        }
    }
}

@Composable
private fun PermissionStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Overline(text = "Izin")
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Beberapa izin dibutuhkan agar gate bisa muncul.",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W600,
                fontSize = 22.sp,
                color = SumiBlack,
                lineHeight = 30.sp,
            ),
        )
        Spacer(Modifier.height(16.dp))
        PermissionRow("Tampilkan di atas aplikasi lain", "Untuk overlay gate.")
        PermissionRow("Bebas battery optimization", "Agar tidak dimatikan OS.")
        PermissionRow("Notifikasi", "Untuk smart-card di lock screen.")
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Setelah onboarding, buka tab Atur untuk grant izin satu per satu.",
            style = TextStyle(fontFamily = Manrope, fontSize = 13.sp, color = SumiLight),
        )
    }
}

@Composable
private fun PermissionRow(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(8.dp)
                .clip(CircleShape),
        ) {
            androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) { drawCircle(HankoRed) }
        }
        Column {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = Manrope,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                    color = SumiDark,
                ),
            )
            Text(
                text = subtitle,
                style = TextStyle(fontFamily = Manrope, fontSize = 12.sp, color = SumiMid),
            )
        }
    }
}

@Composable
private fun PinStep(
    state: OnboardingProgress,
    onPinChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Overline(text = "PIN Darurat")
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Set PIN 4 digit.",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W600,
                fontSize = 24.sp,
                color = SumiBlack,
            ),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Untuk bypass gate hardcore. Disimpan ter-hash di device, tidak ke server.",
            style = TextStyle(
                fontFamily = Manrope,
                fontSize = 13.sp,
                color = SumiMid,
                lineHeight = 18.sp,
            ),
        )
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = state.pinDigits,
            onValueChange = onPinChange,
            label = { Text("PIN", color = SumiMid) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HankoRed,
                unfocusedBorderColor = SumiLight.copy(alpha = 0.4f),
            ),
            textStyle = TextStyle(fontFamily = Manrope, fontSize = 18.sp, color = SumiDark),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.pinConfirm,
            onValueChange = onConfirmChange,
            label = { Text("Konfirmasi PIN", color = SumiMid) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HankoRed,
                unfocusedBorderColor = SumiLight.copy(alpha = 0.4f),
            ),
            textStyle = TextStyle(fontFamily = Manrope, fontSize = 18.sp, color = SumiDark),
        )
        if (state.pinError != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = state.pinError,
                style = TextStyle(
                    fontFamily = Manrope,
                    fontSize = 12.sp,
                    color = HankoRed,
                ),
            )
        }
    }
}

@Composable
private fun DailyGoalStep(state: OnboardingProgress, onChange: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Overline(text = "Target harian")
        Spacer(Modifier.height(8.dp))
        Text(
            text = "${state.dailyGoal}",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W700,
                fontSize = 80.sp,
                color = SumiBlack,
                letterSpacing = (-3).sp,
            ),
        )
        Text(
            text = "kartu / hari",
            style = TextStyle(fontFamily = Manrope, fontSize = 14.sp, color = SumiMid),
        )
        Spacer(Modifier.height(20.dp))
        Slider(
            value = state.dailyGoal.toFloat(),
            onValueChange = { onChange(it.toInt()) },
            valueRange = 5f..50f,
            steps = 8, // 5, 10, 15, 20, 25, 30, 35, 40, 45, 50
            colors = SliderDefaults.colors(
                thumbColor = HankoRed,
                activeTrackColor = HankoRed,
                inactiveTrackColor = SumiLight.copy(alpha = 0.3f),
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("5", style = TextStyle(fontFamily = Manrope, fontSize = 11.sp, color = SumiLight))
            Text("50", style = TextStyle(fontFamily = Manrope, fontSize = 11.sp, color = SumiLight))
        }
    }
}

@Composable
private fun DoneStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HankoStamp(text = "始", size = HankoSize.Lg)
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Mari mulai.",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W600,
                fontSize = 36.sp,
                color = SumiBlack,
            ),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Tomo Sensei sekarang siap menemani 2 bulan ke depan.",
            style = TextStyle(
                fontFamily = Manrope,
                fontSize = 14.sp,
                color = SumiMid,
                lineHeight = 20.sp,
            ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DeviceCheckStep(state: OnboardingProgress) {
    val r = state.deviceCheck
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Overline(text = "Cek perangkat")
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Apakah HP-mu sanggup?",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W600,
                fontSize = 24.sp,
                color = SumiBlack,
            ),
        )
        Spacer(Modifier.height(16.dp))
        if (r == null) {
            Text("Memeriksa…", style = TextStyle(fontFamily = Manrope, color = SumiMid))
        } else {
            CheckRow("Android 12+", "API ${android.os.Build.VERSION.SDK_INT}", r.androidVersionOk)
            CheckRow("RAM ≥ 8 GB", "${"%.1f".format(r.ramGb)} GB", r.ramOk)
            CheckRow("Storage ≥ 5 GB free", "${"%.1f".format(r.storageFreeGb)} GB", r.storageOk)
            CheckRow("ABI arm64-v8a", r.abi, r.abiOk)
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (r.allGreen) "Spec sudah cukup buat Gemma 4 E4B."
                       else "Sebagian belum kompatibel — gate engine + drill tetap jalan, AI tidak.",
                style = TextStyle(
                    fontFamily = Manrope,
                    fontSize = 12.sp,
                    color = SumiMid,
                    lineHeight = 18.sp,
                ),
            )
        }
    }
}

@Composable
private fun CheckRow(label: String, value: String, ok: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = Manrope,
                fontWeight = FontWeight.W600,
                fontSize = 13.sp,
                color = SumiDark,
            ),
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = value,
                style = TextStyle(fontFamily = Manrope, fontSize = 12.sp, color = SumiLight),
            )
            Text(
                text = if (ok) "✓" else "✗",
                style = TextStyle(
                    fontFamily = Manrope,
                    fontWeight = FontWeight.W700,
                    fontSize = 14.sp,
                    color = if (ok) SuccessMoss else HankoRed,
                ),
            )
        }
    }
}

@Composable
private fun GatedAppsStep(state: OnboardingProgress, onToggle: (String) -> Unit) {
    val candidates = listOf(
        Triple("com.zhiliaoapp.musically", "TikTok", "🎵"),
        Triple("com.instagram.android", "Instagram", "📷"),
        Triple("com.google.android.youtube", "YouTube", "▶️"),
        Triple("com.twitter.android", "X / Twitter", "𝕏"),
        Triple("com.shopee.id", "Shopee", "🛒"),
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Overline(text = "App distraksi")
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Pilih app yang akan di-gate.",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W600,
                fontSize = 22.sp,
                color = SumiBlack,
            ),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Saat kamu buka app ini, kartu kosakata muncul dulu.",
            style = TextStyle(fontFamily = Manrope, fontSize = 12.sp, color = SumiMid),
        )
        Spacer(Modifier.height(16.dp))
        candidates.forEach { (pkg, name, emoji) ->
            val checked = pkg in state.gatedApps
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(text = emoji, fontSize = 20.sp)
                    Text(
                        text = name,
                        style = TextStyle(
                            fontFamily = Manrope,
                            fontWeight = FontWeight.W600,
                            fontSize = 14.sp,
                            color = SumiDark,
                        ),
                    )
                }
                androidx.compose.material3.Switch(
                    checked = checked,
                    onCheckedChange = { onToggle(pkg) },
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedTrackColor = HankoRed,
                        uncheckedTrackColor = SumiLight.copy(alpha = 0.4f),
                    ),
                )
            }
        }
    }
}

@Composable
private fun ModelDownloadStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Overline(text = "Sensei AI")
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Gemma 4 model — 3.6 GB.",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W600,
                fontSize = 24.sp,
                color = SumiBlack,
            ),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Sensei chat dan Foto Sensei butuh model on-device. Kita download nanti — buka tab Atur → Sensei AI saat HP terhubung Wi-Fi.",
            style = TextStyle(
                fontFamily = Manrope,
                fontSize = 14.sp,
                color = SumiMid,
                lineHeight = 20.sp,
            ),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Sambil tunggu download, drill kosakata + gate engine sudah bisa jalan.",
            style = TextStyle(
                fontFamily = Manrope,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                fontSize = 13.sp,
                color = SumiLight,
            ),
        )
    }
}

@Composable
private fun SimulationStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Overline(text = "Simulasi")
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Gate pertama akan muncul saat unlock.",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W600,
                fontSize = 22.sp,
                color = SumiBlack,
                lineHeight = 30.sp,
            ),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Setelah onboarding selesai, lock HP lalu unlock — kartu kosakata akan muncul. Jawab tau / lupa untuk dismiss.",
            style = TextStyle(
                fontFamily = Manrope,
                fontSize = 14.sp,
                color = SumiMid,
                lineHeight = 20.sp,
            ),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Kalau terlalu mengganggu, turunkan intensity di tab Atur kapan saja.",
            style = TextStyle(fontFamily = Manrope, fontSize = 12.sp, color = SumiLight),
        )
    }
}

@Composable
private fun BottomNavRow(
    state: OnboardingProgress,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (state.step > 0) {
            TextButton(onClick = onBack) {
                Text(
                    text = "Kembali",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = Manrope,
                        color = SumiMid,
                    ),
                )
            }
        } else {
            Spacer(Modifier.size(1.dp))
        }
        val isLast = state.step >= TOTAL_STEPS - 1
        val canAdvance = when (state.step) {
            3 -> state.preset != null
            else -> true
        }
        Button(
            onClick = if (isLast) onFinish else onNext,
            enabled = canAdvance,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLast) SuccessMoss else HankoRed,
                contentColor = WashiCreamLight,
                disabledContainerColor = SumiLight.copy(alpha = 0.4f),
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 28.dp, vertical = 14.dp,
            ),
        ) {
            Text(
                text = if (isLast) "Selesai" else "Lanjut",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = Manrope,
                    fontWeight = FontWeight.W700,
                ),
            )
        }
    }
}
