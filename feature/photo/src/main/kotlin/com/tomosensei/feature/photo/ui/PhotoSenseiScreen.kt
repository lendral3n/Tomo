package com.tomosensei.feature.photo.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tomosensei.core.ai.PhotoAnalysis
import com.tomosensei.core.ai.VocabHighlight
import com.tomosensei.core.designsystem.components.Overline
import com.tomosensei.core.designsystem.components.WashiBackground
import com.tomosensei.core.designsystem.components.WashiCard
import com.tomosensei.core.designsystem.theme.HankoRed
import com.tomosensei.core.designsystem.theme.Manrope
import com.tomosensei.core.designsystem.theme.ShipporiMincho
import com.tomosensei.core.designsystem.theme.SumiBlack
import com.tomosensei.core.designsystem.theme.SumiDark
import com.tomosensei.core.designsystem.theme.SumiLight
import com.tomosensei.core.designsystem.theme.SumiMid
import com.tomosensei.core.designsystem.theme.WashiCreamLight
import com.tomosensei.core.designsystem.theme.YamiDeep
import com.tomosensei.core.designsystem.theme.ZenKakuGothic
import com.tomosensei.feature.photo.PhotoSenseiState
import com.tomosensei.feature.photo.PhotoSenseiViewModel
import java.io.ByteArrayOutputStream

@Composable
fun PhotoSenseiScreen(
    modifier: Modifier = Modifier,
    viewModel: PhotoSenseiViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted },
    )
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    WashiBackground(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize().padding(top = 52.dp, bottom = 100.dp)) {
            Header()
            Spacer(Modifier.height(8.dp))
            when (val s = state) {
                PhotoSenseiState.Idle -> CameraStage(
                    hasPermission = hasCameraPermission,
                    onCapture = { bmp -> viewModel.analyze(bmp) },
                    onGrant = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                )
                is PhotoSenseiState.Analyzing -> AnalyzingStage(streamed = s.raw)
                is PhotoSenseiState.Result -> ResultStage(
                    analysis = s.analysis,
                    onAgain = viewModel::reset,
                )
                is PhotoSenseiState.Failed -> FailedStage(
                    message = s.message,
                    onRetry = viewModel::reset,
                )
            }
        }
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Foto Sensei",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                color = SumiBlack,
            ),
        )
        Text(
            text = "撮 · multi-modal",
            style = TextStyle(
                fontFamily = Manrope,
                fontWeight = FontWeight.W500,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = SumiLight,
            ),
        )
    }
}

@Composable
private fun CameraStage(
    hasPermission: Boolean,
    onCapture: (Bitmap) -> Unit,
    onGrant: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(YamiDeep),
            contentAlignment = Alignment.Center,
        ) {
            if (hasPermission) {
                CameraPreview(onCapture = onCapture)
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Izinkan akses kamera dulu",
                        style = TextStyle(
                            fontFamily = Manrope,
                            fontSize = 14.sp,
                            color = WashiCreamLight,
                        ),
                    )
                    Button(
                        onClick = onGrant,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HankoRed,
                            contentColor = WashiCreamLight,
                        ),
                    ) { Text("Beri izin") }
                }
            }
        }
        Text(
            text = "Foto teks Jepang — menu, signage, manga, screenshot. Sensei akan ekstrak + translate + breakdown grammar.",
            style = TextStyle(
                fontFamily = Manrope,
                fontSize = 12.sp,
                color = SumiMid,
                lineHeight = 18.sp,
            ),
            modifier = Modifier.padding(horizontal = 8.dp),
        )
    }
}

@Composable
private fun CameraPreview(onCapture: (Bitmap) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().build() }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val selector = CameraSelector.DEFAULT_BACK_CAMERA
                    runCatching {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, selector, preview, imageCapture,
                        )
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
        )
        Button(
            onClick = {
                imageCapture.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val bmp = imageProxyToBitmap(image)
                            image.close()
                            onCapture(bmp)
                        }
                        override fun onError(exception: ImageCaptureException) = Unit
                    },
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .size(72.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = HankoRed,
                contentColor = WashiCreamLight,
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        ) {
            Text(text = "撮", fontSize = 24.sp)
        }
    }
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

@Composable
private fun AnalyzingStage(streamed: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(40.dp))
        CircularProgressIndicator(color = HankoRed)
        Text(
            text = "Sensei sedang membaca…",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W600,
                fontSize = 18.sp,
                color = SumiBlack,
            ),
        )
        if (streamed.isNotBlank()) {
            WashiCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = streamed.take(400) + " ▍",
                    style = TextStyle(
                        fontFamily = Manrope,
                        fontSize = 11.sp,
                        color = SumiLight,
                        lineHeight = 16.sp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun ResultStage(analysis: PhotoAnalysis, onAgain: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        WashiCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Overline(text = "Teks Jepang")
                Text(
                    text = analysis.extractedText.ifBlank { "(tidak terdeteksi)" },
                    style = TextStyle(
                        fontFamily = ShipporiMincho,
                        fontWeight = FontWeight.W600,
                        fontSize = 22.sp,
                        color = SumiBlack,
                        lineHeight = 30.sp,
                    ),
                )
                if (analysis.reading.isNotBlank()) {
                    Text(
                        text = analysis.reading,
                        style = TextStyle(
                            fontFamily = ZenKakuGothic,
                            fontSize = 14.sp,
                            color = SumiMid,
                            letterSpacing = 0.8.sp,
                        ),
                    )
                }
            }
        }
        WashiCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Overline(text = "Terjemahan")
                Text(
                    text = analysis.translation,
                    style = TextStyle(
                        fontFamily = Manrope,
                        fontWeight = FontWeight.W500,
                        fontSize = 16.sp,
                        color = SumiDark,
                        lineHeight = 22.sp,
                    ),
                )
            }
        }
        if (analysis.grammarNotes.isNotEmpty()) {
            WashiCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Overline(text = "Catatan grammar")
                    analysis.grammarNotes.forEach { note ->
                        Text(
                            text = "· $note",
                            style = TextStyle(
                                fontFamily = Manrope,
                                fontSize = 13.sp,
                                color = SumiMid,
                                lineHeight = 18.sp,
                            ),
                        )
                    }
                }
            }
        }
        if (analysis.vocabHighlights.isNotEmpty()) {
            WashiCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Overline(text = "Kosakata")
                    analysis.vocabHighlights.forEach { v -> VocabRow(v) }
                }
            }
        }
        OutlinedButton(
            onClick = onAgain,
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
        ) { Text("Foto lagi", style = MaterialTheme.typography.labelLarge.copy(fontFamily = Manrope)) }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun VocabRow(v: VocabHighlight) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = v.jp,
                    style = TextStyle(
                        fontFamily = ShipporiMincho,
                        fontWeight = FontWeight.W600,
                        fontSize = 18.sp,
                        color = SumiBlack,
                    ),
                )
                Text(
                    text = v.reading,
                    style = TextStyle(
                        fontFamily = ZenKakuGothic,
                        fontSize = 11.sp,
                        color = SumiLight,
                    ),
                )
            }
            Text(
                text = v.meaning,
                style = TextStyle(
                    fontFamily = Manrope,
                    fontSize = 12.sp,
                    color = SumiMid,
                ),
            )
        }
        Text(
            text = v.level,
            style = TextStyle(
                fontFamily = Manrope,
                fontWeight = FontWeight.W700,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = HankoRed,
            ),
        )
    }
}

@Composable
private fun FailedStage(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Overline(text = "Gagal")
        Text(
            text = message,
            style = TextStyle(
                fontFamily = Manrope,
                fontSize = 14.sp,
                color = HankoRed,
            ),
        )
        OutlinedButton(onClick = onRetry, shape = CircleShape) {
            Text("Coba lagi", style = MaterialTheme.typography.labelLarge.copy(fontFamily = Manrope))
        }
    }
}
