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
import com.duel.crydecoder.data.AudioClassifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClassifierViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ClassifierUiState())
    val uiState = _uiState.asStateFlow()

    private var audioClassifier: AudioClassifier? = null
    private var audioRecord: AudioRecord? = null

    private val sampleRate = 22050
    private val durationSecs = 6
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                audioClassifier = AudioClassifier(getApplication())
            } catch (e: Exception) {
                Log.e("ViewModel", "Failed to initialize AudioClassifier in ViewModel", e)
                _uiState.update { it.copy(resultText = "Error: Model failed to load.") }
            }
        }
    }

    fun onRecordClick() {
        if (!_uiState.value.isRecording) {
            startRecording()
        }
    }

    private fun startRecording() {
        if (audioClassifier == null) {
            _uiState.update { it.copy(resultText = "Error: Classifier not ready.") }
            return
        }
        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            _uiState.update { it.copy(resultText = "Audio permission not granted.") }
            return
        }

        _uiState.update { it.copy(isRecording = true, resultText = "Listening for 6 seconds...") }

        viewModelScope.launch(Dispatchers.IO) {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            audioRecord?.startRecording()
            delay(durationSecs * 1000L)

            if (_uiState.value.isRecording) {
                stopRecordingAndClassify()
            }
        }
    }

    private fun stopRecordingAndClassify() {
        val recorder = audioRecord ?: return

        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update { it.copy(isRecording = false, isLoading = true) }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val audioData = ShortArray(sampleRate * durationSecs)
            recorder.read(audioData, 0, audioData.size)

            val result = audioClassifier?.classify(audioData) ?: "Classification failed."

            recorder.stop()
            recorder.release()
            audioRecord = null

            _uiState.update { it.copy(isLoading = false, resultText = result) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioClassifier?.close()
        audioRecord?.release()
    }
}