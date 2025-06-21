package com.duel.crydecoder.data

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.flex.FlexDelegate
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class AudioClassifier(
    private val context: Context,
    private val modelPath: String = "baby_cry_model.tflite",
) {
    private val N_MFCC = 40
    private val INPUT_TIME_STEPS = 259
    private var interpreter: Interpreter
    private val labels: List<String>

    init {
        try {
            Log.d("AudioClassifier", "Initializing classifier with FlexDelegate.")
            val options = Interpreter.Options()
            options.addDelegate(FlexDelegate())
            interpreter = Interpreter(loadModelFile(), options)
            labels = context.assets.open("cry_classes.txt").bufferedReader().readLines()
            Log.d("AudioClassifier", "✅ Classifier initialized successfully.")
        } catch (e: Exception) {
            Log.e("AudioClassifier", "TFLite failed to initialize. See error below.", e)
            throw IllegalStateException("Failed to initialize TFLite Classifier", e)
        }
    }

    fun classify(audioData: ShortArray): String {
        try {
            val floatAudioData = audioData.map { it / 32768.0f }.toFloatArray()
            val mfccs = extractMFCCs(floatAudioData)
            val inputBuffer = mfccs.reshape(1, N_MFCC, INPUT_TIME_STEPS, 1)
            val outputBuffer = Array(1) { FloatArray(labels.size) }
            interpreter.run(inputBuffer, outputBuffer)
            val maxIndex = outputBuffer[0].indices.maxByOrNull { outputBuffer[0][it] } ?: -1
            return if (maxIndex != -1) "Prediction: ${labels[maxIndex]}" else "Could not classify."
        } catch (e: Exception) {
            Log.e("AudioClassifier", "Error during classification", e)
            return "Error during classification."
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        return FileInputStream(fileDescriptor.fileDescriptor).channel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }

    private fun extractMFCCs(audioData: FloatArray): FloatArray {
        Log.w("MFCC", "Using placeholder MFCC data. This will not produce correct results.")
        return FloatArray(1 * N_MFCC * INPUT_TIME_STEPS)
    }

    private fun FloatArray.reshape(d1: Int, d2: Int, d3: Int, d4: Int): Array<Array<Array<FloatArray>>> {
        require(d1 * d2 * d3 * d4 == size) { "Array size ($size) does not match target shape" }
        val result = Array(d1) { Array(d2) { Array(d3) { FloatArray(d4) } } }
        var index = 0
        for (i in 0 until d1) { for (j in 0 until d2) { for (k in 0 until d3) { for (l in 0 until d4) {
            result[i][j][k][l] = this[index++]
        }}}}
        return result
    }

    fun close() {
        interpreter.close()
    }
}