package com.roman.lab1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.roman.lab1.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                binding.errorMessage.text = getString(R.string.fill_in_fields)
                return@setOnClickListener
            }
            attemptLogin(username, password)
        }
    }

    private fun attemptLogin(username: String, password: String) {
        lifecycleScope.launch {
            if (viewModel.isUserExist(username)) {
                if (viewModel.isPasswordCorrect(username, password)) {
                    createToast(getString(R.string.welcome_back, username))
                    findNavController().navigate(R.id.action_loginFragment_to_playFragment)
                } else {
                    binding.errorMessage.text = getString(R.string.wrong_password)
                }
            } else {
                viewModel.addUser(username, password)
                createToast(getString(R.string.sign_up))
                findNavController().navigate(R.id.action_loginFragment_to_playFragment)
            }
        }
    }

    private fun createToast(message: String) {
        Toast.makeText(requireActivity().applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}