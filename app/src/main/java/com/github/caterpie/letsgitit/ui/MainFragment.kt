package com.github.caterpie.letsgitit.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.caterpie.letsgitit.data.ChallengeRepository
import com.github.caterpie.letsgitit.databinding.FragmentMainBinding
import java.lang.Integer.min

class MainFragment : Fragment() {
    private val binding by lazy { FragmentMainBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<MainViewModel> {
        MainViewModelFactory(ChallengeRepository())
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainChallengeValue.addTextChangedListener(viewModel.setChallengeEvent())
        binding.mainChallengeValue.setOnEditorActionListener(keyInputDoneListener())
        binding.mainChallengeTitleValue.setOnEditorActionListener(keyInputDoneListener())
    }

    private fun keyInputDoneListener(): TextView.OnEditorActionListener {
        return TextView.OnEditorActionListener { textView, action, _ ->
            var result = false
            textView?.let { tv ->
                if (action == EditorInfo.IME_ACTION_DONE) {
                    tv.clearFocus()
                    tv.isFocusable = false
                    tv.isFocusableInTouchMode = true
                    tv.isFocusable = true
                    val imm =
                        requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(tv.windowToken, 0)
                    result = true
                }
            }
            result
        }
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onGoing.observe(viewLifecycleOwner) {
            binding.mainChallengeOngoingValue.text = "$it"
            binding.mainChallengeProgress.progress = min(it, viewModel.challenge.value ?: 365)
        }
        viewModel.challenge.observe(viewLifecycleOwner) {
            binding.mainChallengeProgress.max = it
            binding.mainChallengeProgress.progress = min(it, viewModel.onGoing.value ?: 0)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment()
    }
}