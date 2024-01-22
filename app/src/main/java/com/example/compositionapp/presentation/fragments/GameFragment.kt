package com.example.compositionapp.presentation.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.compositionapp.R
import com.example.compositionapp.databinding.FragmentGameBinding
import com.example.compositionapp.domain.entity.GameResult
import com.example.compositionapp.domain.entity.Level
import com.example.compositionapp.presentation.GameViewModel
import com.example.compositionapp.presentation.GameViewModelFactory

class GameFragment : Fragment() {

    private val viewModelFactory by lazy {
        GameViewModelFactory(requireActivity().application, level)
    }

    private val gameViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
    }

    private lateinit var level: Level

    private val tvOptions by lazy {
        mutableListOf<TextView>().apply {
            with(binding) {
                add(tvOption1)
                add(tvOption2)
                add(tvOption3)
                add(tvOption4)
                add(tvOption5)
                add(tvOption6)
            }
        }
    }

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelObserver()
        setClickListenersToOptions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setClickListenersToOptions() {
        for (tvOption in tvOptions) {
            tvOption.setOnClickListener {
                gameViewModel.chooseAnswer(tvOption.text.toString().toInt())
            }
        }
    }

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null)
            .commit()
    }

    private fun viewModelObserver() {
        gameViewModel.question.observe(viewLifecycleOwner) {
            with(binding) {
                tvSum.text = it.sum.toString()
                tvLeftNumber.text = it.visibleNumber.toString()
                for (i in 0 until tvOptions.size) {
                    tvOptions[i].text = it.options[i].toString()
                }
            }
        }

        gameViewModel.formattedTimeLiveData.observe(viewLifecycleOwner) {
            binding.tvTimer.text = it
        }

        gameViewModel.progressAnswers.observe(viewLifecycleOwner) {
            binding.tvAnswerProgress.text = it
        }

        gameViewModel.percentOfRightAnswer.observe(viewLifecycleOwner) {
            binding.progressBar.setProgress(it, true)
        }

        gameViewModel.enoughCountOfRightAnswers.observe(viewLifecycleOwner) {
            binding.tvAnswerProgress.setTextColor(getColorByState(it))
        }

        gameViewModel.enoughPercentOfRightAnswers.observe(viewLifecycleOwner) {
            val color = getColorByState(it)
            binding.progressBar.progressTintList = ColorStateList.valueOf(color)
        }

        gameViewModel.minPercent.observe(viewLifecycleOwner) {
            binding.progressBar.secondaryProgress = it
        }

        gameViewModel.gameResult.observe(viewLifecycleOwner) {
            launchGameFinishedFragment(it)
        }
    }

    private fun getColorByState(state: Boolean): Int {
        val color =  if (state) {
            android.R.color.holo_green_dark
        } else {
            android.R.color.holo_red_dark
        }
        return ContextCompat.getColor(requireContext(), color)
    }


    private fun parseArgs() {
        requireArguments().getParcelable<Level>(KEY_LEVEL)?.let {
            level = it
        }
    }

    companion object {
        private const val KEY_LEVEL = "level"
        const val NAME = "GameFragment"

        fun newInstance(level: Level) =
            GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LEVEL, level)
                }
            }
    }

}