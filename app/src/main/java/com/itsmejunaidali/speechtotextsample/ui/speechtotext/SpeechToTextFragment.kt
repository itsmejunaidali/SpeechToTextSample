package com.itsmejunaidali.speechtotextsample.ui.speechtotext

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.itsmejunaidali.speechtotextsample.R
import com.itsmejunaidali.speechtotextsample.databinding.FragmentSpeechToTextBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.util.*

class SpeechToTextFragment : Fragment(), View.OnClickListener, RecognitionListener {

    private lateinit var binding: FragmentSpeechToTextBinding
    private val viewModel: SpeechToTextViewModel by viewModels()

    private lateinit var recognizerIntent: Intent
    private lateinit var speechRecognizer: SpeechRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT,getString(R.string.speech_prompt))
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)
        speechRecognizer.setRecognitionListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentSpeechToTextBinding.inflate(inflater,container,false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.clickListener = this

        viewModel.setSpeechStateLabel(getString(R.string.tap_to_speak))

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }

    // region Listeners
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_speak -> {
                // check and ask for microphone permission
                Dexter.withContext(activity)
                    .withPermission(Manifest.permission.RECORD_AUDIO)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse?) {
                            speechRecognizer.startListening(recognizerIntent)
                        }

                        override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse?) {}

                        override fun onPermissionRationaleShouldBeShown(permissionRequest: PermissionRequest?, permissionToken: PermissionToken?) {
                            permissionToken?.continuePermissionRequest()
                        }
                    }).check()
            }
        }
    }

    override fun onReadyForSpeech(params: Bundle?) {
        viewModel.setSpeechStateLabel(getString(R.string.listening))
    }

    override fun onBeginningOfSpeech() {}

    override fun onRmsChanged(rmsdB: Float) {}

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        viewModel.setSpeechStateLabel(getString(R.string.tap_to_speak))
    }

    override fun onError(error: Int) {
        Toast.makeText(activity,R.string.something_went_wrong,Toast.LENGTH_LONG).show()
    }

    override fun onResults(results: Bundle?) {
        val result = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        result?.let {
            viewModel.setSpeechInput(it[0])
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {}

    override fun onEvent(eventType: Int, params: Bundle?) {}

    // endregion

}