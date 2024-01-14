package com.example.compositionapp.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.compositionapp.R
import com.example.compositionapp.databinding.FragmentChooseLevelBinding
import com.example.compositionapp.domain.entity.Level

class ChooseLevelFragment : Fragment() {

    private var _binding: FragmentChooseLevelBinding? = null
    private val binding: FragmentChooseLevelBinding
        get() = _binding ?: throw RuntimeException("FragmentChooseLevelBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clickListeners() {
        with(binding) {
            butLevelTest.setOnClickListener {
                launchGameFragment(Level.TEST)
            }

            butLevelEasy.setOnClickListener {
                launchGameFragment(Level.EASY)
            }

            butLevelNormal.setOnClickListener {
                launchGameFragment(Level.NORMAL)
            }

            butLevelHard.setOnClickListener {
                launchGameFragment(Level.HARD)
            }
        }
    }

    private fun launchGameFragment(level: Level) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GameFragment.newInstance(level))
            .addToBackStack(GameFragment.NAME)
            .commit()
    }

    companion object {
        const val NAME = "ChooseLevelFragment"
        fun newInstance() = ChooseLevelFragment()
    }

}