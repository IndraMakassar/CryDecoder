package com.duel.crydecoder.ui.classifier

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duel.crydecoder.data.RetrofitInstance
import com.duel.crydecoder.ui.history.HistoryUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Job

class ClassifierViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ClassifierUiState())
    val uiState = _uiState.asStateFlow()

    private var audioRecord: AudioRecord? = null

    private val sampleRate = 22050
    private val durationSecs = 6
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

    private var recordingJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }

    fun onRecordClick(onResult: (HistoryUiState) -> Unit) {
        if (_uiState.value.isRecording) {
            recordingJob?.cancel()
            stopRecordingOnly()
        } else {
            startRecording(onResult)
        }
    }
    private fun stopRecordingOnly() {
        audioRecord?.apply {
            stop()
            release()
        }
        audioRecord = null
        recordingJob = null

        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update {
                it.copy(
                    isRecording = false,
                    isLoading = false,
                    resultText = "Recording canceled."
                )
            }
        }
    }


    private fun startRecording(onResult: (HistoryUiState) -> Unit) {
        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            _uiState.update { it.copy(resultText = "Audio permission not granted.") }
            return
        }

        _uiState.update { it.copy(isRecording = true, isResultReady = false, resultText = "Listening for 6 seconds...") }

        recordingJob = viewModelScope.launch(Dispatchers.IO) {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            audioRecord?.startRecording()

            try {
                delay(durationSecs * 1000L)
                if (_uiState.value.isRecording) {
                    stopRecordingAndClassify(onResult)
                }
            } catch (e: CancellationException) {
                // Recording was stopped early
            }
        }
    }

    private fun stopRecordingAndClassify(onResult: (HistoryUiState) -> Unit) {
        val recorder = audioRecord ?: return

        recordingJob?.cancel()
        recordingJob = null

        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update { it.copy(isRecording = false, isLoading = true) }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val audioData = ShortArray(sampleRate * durationSecs)
            recorder.read(audioData, 0, audioData.size)

            recorder.stop()
            recorder.release()
            audioRecord = null

            // Save to WAV
            val file = File(getApplication<Application>().cacheDir, "recording.wav")
            writeWavFile(audioData, file, sampleRate)

            try {
                val requestBody = file.asRequestBody("audio/wav".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("audio", file.name, requestBody)

                val response = RetrofitInstance.api.uploadAudio(body)

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    val prediction = result.prediction
                    val confidence = (result.confidence * 100).toInt()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isResultReady = true,
                            resultText = "Prediction: ${result.prediction} (${(result.confidence * 100).toInt()}%)"
                        )
                    }
                    onResult(
                        HistoryUiState(
                            title = prediction,
                            explanation = getExplanation(prediction),
                            timestamp = System.currentTimeMillis()
                        )
                    )

                } else {
                    _uiState.update { it.copy(isLoading = false, resultText = "Server error: ${response.code()}") }
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error uploading audio", e)
                _uiState.update { it.copy(isLoading = false, resultText = "Failed to upload: ${e.localizedMessage}") }
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        audioRecord?.release()
    }
}

fun writeWavFile(audioData: ShortArray, file: File, sampleRate: Int) {
    val byteBuffer = ByteArray(audioData.size * 2)
    for (i in audioData.indices) {
        byteBuffer[i * 2] = (audioData[i].toInt() and 0x00FF).toByte()
        byteBuffer[i * 2 + 1] = ((audioData[i].toInt() shr 8) and 0xFF).toByte()
    }

    val outputStream = FileOutputStream(file)
    val dataSize = byteBuffer.size
    val totalDataLen = dataSize + 36
    val channels = 1
    val byteRate = 16 * sampleRate * channels / 8

    val header = ByteArray(44)
    val headerData = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)
    headerData.put("RIFF".toByteArray())
    headerData.putInt(totalDataLen)
    headerData.put("WAVE".toByteArray())
    headerData.put("fmt ".toByteArray())
    headerData.putInt(16) // Subchunk1Size for PCM
    headerData.putShort(1) // AudioFormat = PCM
    headerData.putShort(channels.toShort())
    headerData.putInt(sampleRate)
    headerData.putInt(byteRate)
    headerData.putShort((channels * 16 / 8).toShort()) // Block align
    headerData.putShort(16) // Bits per sample
    headerData.put("data".toByteArray())
    headerData.putInt(dataSize)

    outputStream.write(header)
    outputStream.write(byteBuffer)
    outputStream.close()
}

private fun getExplanation(label: String): String {
    return when (label.lowercase()) {
        "belly_pain" -> "The baby may be experiencing stomach pain."
        "burping" -> "The baby might need to burp."
        "discomfort" -> "The baby feels uncomfortable."
        "hungry" -> "The baby is hungry and needs to be fed."
        "tired" -> "The baby is tired and likely needs sleep."
        else -> "No specific cry detected."
    }
}

