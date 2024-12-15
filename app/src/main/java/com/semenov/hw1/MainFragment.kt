package com.semenov.hw1

import android.R.attr.data
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.semenov.hw1.databinding.FragmentMainBinding


class MainFragment: Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        fragmentTextUpdateObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Setup the button in our fragment to call getUpdatedText method in viewModel
    private fun setupClickListeners() {
        binding.getRandomNum.setOnClickListener {
            viewModel.getRandomNum(binding.meanVal.text.toString(), binding.varianceValue.text.toString())
        }
    }

    // Observer is waiting for viewModel to update our UI
    private fun fragmentTextUpdateObserver() {
        viewModel.numForTextView.observe(viewLifecycleOwner, Observer { updatedNum ->
            when (updatedNum) {
                ERR_EMPTY -> Toast.makeText(activity, R.string.err_empty, Toast.LENGTH_SHORT).show()
                ERR_INF -> Toast.makeText(activity, R.string.err_inf, Toast.LENGTH_SHORT).show()
                else -> binding.randomNumberResult.text = updatedNum.toString()
            }
        })
    }
}
