package com.roman.lab1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.roman.lab1.databinding.FragmentPlayBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayFragment : Fragment() {

    private lateinit var binding: FragmentPlayBinding
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPlayBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.username.text = viewModel.currentUser?.username

        viewModel.gameState.observe(viewLifecycleOwner) {
            binding.item1.text = it[0].text
            binding.item2.text = it[1].text
            binding.item3.text = it[2].text
            binding.item4.text = it[3].text
            binding.item5.text = it[4].text
            binding.item6.text = it[5].text
            binding.item7.text = it[6].text
            binding.item8.text = it[7].text
            binding.item9.text = it[8].text
        }

        viewModel.isUserWin.observe(viewLifecycleOwner) {
            if (it != null) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Game Over")
                    .setMessage(if (it) "You Win!" else "You Lose!")
                    .setPositiveButton("Restart") { _, _ ->
                        viewModel.resetGame()
                    }
                    .setNegativeButton("OK") { _, _ ->
                        requireActivity().finish()
                    }
                    .setCancelable(false)
                    .show()
            }
        }

        binding.exit.setOnClickListener {
            requireActivity().finish()
        }

        binding.restart.setOnClickListener {
            viewModel.resetGame()
        }

        binding.item1.setOnClickListener {
            tryToMakeMove(0)
        }

        binding.item2.setOnClickListener {
            tryToMakeMove(1)
        }

        binding.item3.setOnClickListener {
            tryToMakeMove(2)
        }

        binding.item4.setOnClickListener {
            tryToMakeMove(3)
        }

        binding.item5.setOnClickListener {
            tryToMakeMove(4)
        }

        binding.item6.setOnClickListener {
            tryToMakeMove(5)
        }

        binding.item7.setOnClickListener {
            tryToMakeMove(6)
        }

        binding.item8.setOnClickListener {
            tryToMakeMove(7)
        }

        binding.item9.setOnClickListener {
            tryToMakeMove(8)
        }
    }

    private fun tryToMakeMove(index: Int) {
        viewModel.gameState.value?.let {
            if (it[index] == SquareState.EMPTY) {
                viewModel.changeSquareState(index, SquareState.PLAYER)
                if (viewModel.isUserWin.value == null)
                    viewModel.makeEnemyMove()
            }
        }
    }
}