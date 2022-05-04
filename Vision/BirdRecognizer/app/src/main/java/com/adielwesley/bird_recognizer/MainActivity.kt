package com.adielwesley.bird_recognizer

import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.adielwesley.bird_recognizer.databinding.ActivityMainBinding
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var imageClassifier: ImageClassifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loadButton.setOnClickListener { onLoadButtonClicked() }
        binding.recognizeButton.setOnClickListener { onRecognizeButtonClicked() }

        initializeImageClassifier()
    }

    private fun onLoadButtonClicked() {
        getContent.launch("image/*")
    }

    private fun onRecognizeButtonClicked() {
        // handle input
        val image = TensorImage.fromBitmap(binding.imageView.drawable.toBitmap())

        // Run model
        val classifications = imageClassifier.classify(image)

        // handle output
        Log.d(TAG, "onRecognizeButtonClicked: hasResult = ${classifications.isNotEmpty()}")
        classifications.firstOrNull()?.categories?.firstOrNull()?.displayName?.let { birdName ->
            binding.textView.text = birdName
        }
    }

    private fun initializeImageClassifier() {
        imageClassifier = ImageClassifier.createFromFile(
            this,
            "lite-model_aiy_vision_classifier_birds_V1_3.tflite"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        imageClassifier.close()
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            Log.d(TAG, "onLoadButtonClicked: $uri")
            binding.imageView.setImageURI(uri)
        }

    companion object {

        private val TAG = MainActivity::class.java.simpleName
    }
}
