package com.github.caterpie.letsgitit

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
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

    /**
     * Challenge에 값을 입력하는 경우 ProgressBar를 변경할 수 있도록 함
     */
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

    /**
     * 글자 입력을 마치고 키패드의 done/enter를 선택 시 포커스를 잃게 만들어 키패드를 내림
     */
    private val dismissFocus =
        TextView.OnEditorActionListener { textView, action, _ ->
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
            result
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
        /**
         * 아래의 viewTreeObserver를 통해 생성 될 뷰의 크기를 얻을 수 있음. onResume 시점에서는 0의 값을 가짐
         */
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
        // TODO: Test code. 참고 후 제거 필요함
        CoroutineScope(Dispatchers.Default).launch {
            for (i in 0 until 100) {
                delay(500)
                onGoing = if (onGoing >= challenge) challenge else onGoing + 1
                binding.mainChallengeProgress.progress = onGoing
                val newX = calculateCheckerPosition(onGoing, progressStart, progressWidth / challenge.toDouble(), checkerWidth / 2)
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