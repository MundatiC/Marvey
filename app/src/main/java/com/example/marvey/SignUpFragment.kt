package com.example.marvey

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.marvey.Extensions.toast
import com.example.marvey.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var auth : FirebaseAuth



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignupBinding.bind(view)
        auth = FirebaseAuth.getInstance()






        binding.btnSignUp.setOnClickListener {

            if (
                binding.etEmailSignUp.text.isNotEmpty() &&
                binding.etNameSignUp.text.isNotEmpty() &&
                binding.etPasswordSignUp.text.isNotEmpty()
            ) {

                  createUser(binding.etEmailSignUp.text.toString(),binding.etPasswordSignUp.text.toString())


            }
        }
        binding.tvNavigateToSignIn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_signInFragmentFragment)
        }


    }

    private fun createUser(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    requireActivity().toast("New User created")

                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_signUpFragment_to_mainFragment)

                }
                else{
                    requireActivity().toast(task.exception!!.localizedMessage)
                }

            }

    }


}