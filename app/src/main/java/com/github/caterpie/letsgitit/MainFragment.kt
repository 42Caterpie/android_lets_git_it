package com.github.caterpie.letsgitit

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.caterpie.letsgitit.databinding.FragmentMainBinding
import kotlinx.coroutines.*
import kotlin.math.roundToInt

class MainFragment : Fragment() {
    private val binding by lazy { FragmentMainBinding.inflate(layoutInflater) }
    private var challenge = 365
    private var progressStart = 0
    private var progressWidth = 0
    private var checkerWidth = 0
    private var onGoing = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    private val challengeWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            challenge = try {
                val result = p0?.toString()?.toInt() ?: 365
                if (result == 0) 365 else result
            } catch (exception: Exception) {
                365
            }
            binding.mainChallengeProgress.max = challenge
            onGoing = if (challenge > onGoing) onGoing else challenge
            binding.mainChallengeOngoingValue.text = onGoing.toString()
            val newX = calculateCheckerPosition(onGoing, progressStart, progressWidth / challenge.toDouble(), checkerWidth / 2)
            val layoutParams = LinearLayout.LayoutParams(
                binding.mainChallengeChecker.width,
                binding.mainChallengeChecker.height
            )
            layoutParams.setMargins(newX, 0, 0, 0)
            binding.mainChallengeChecker.layoutParams = layoutParams
        }
    }

    private val dismissFocus = object : TextView.OnEditorActionListener {
        override fun onEditorAction(textView: TextView?, action: Int, keyEvent: KeyEvent?): Boolean {
            var result = false
            textView?.let { textView ->
                if (action == EditorInfo.IME_ACTION_DONE) {
                    textView.clearFocus()
                    textView.isFocusable = false
                    textView.isFocusableInTouchMode = true
                    textView.isFocusable = true
                    val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(textView.windowToken, 0)
                    result = true
                }
            }
            return result
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainChallengeProgress.max = 365
        binding.mainChallengeValue.addTextChangedListener(challengeWatcher)
        binding.mainChallengeValue.setOnEditorActionListener(dismissFocus)
        binding.mainChallengeTitleValue.setOnEditorActionListener(dismissFocus)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        val viewTreeObserver = binding.mainChallengeProgress.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val obs = binding.mainChallengeProgress.viewTreeObserver
                progressStart = binding.mainChallengeProgress.left
                progressWidth = binding.mainChallengeProgress.width
                checkerWidth = binding.mainChallengeChecker.width
                obs.removeOnGlobalLayoutListener(this)
            }
        })
        CoroutineScope(Dispatchers.Default).launch {
            for (i in 0 until 100) {
                delay(500)
                onGoing = if (onGoing >= challenge) challenge else onGoing + 1
                binding.mainChallengeProgress.progress = onGoing
                val newX = calculateCheckerPosition(onGoing, progressStart, progressWidth / challenge.toDouble(), checkerWidth / 2)
                Log.d("Test", "challenge: $challenge width: $progressWidth start: $progressStart ongoing: $onGoing result: $newX")
                val layoutParams = LinearLayout.LayoutParams(
                    binding.mainChallengeChecker.width,
                    binding.mainChallengeChecker.height
                )
                layoutParams.setMargins(newX, 0, 0, 0)
                withContext(Dispatchers.Main) {
                    binding.mainChallengeOngoingValue.text = onGoing.toString()
                    binding.mainChallengeChecker.layoutParams = layoutParams
                    binding.root.invalidate()
                }
            }
        }
    }

    private fun calculateCheckerPosition(current: Int, start: Int, ratio: Double, offset: Int) : Int {
        val newX = ratio * current + start - offset
        return newX.roundToInt()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment()
    }
}