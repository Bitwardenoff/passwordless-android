package dev.passwordless.sampleapp

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.auth0.android.jwt.JWT
import dagger.hilt.android.AndroidEntryPoint
import dev.passwordless.android.PasswordlessClient
import dev.passwordless.sampleapp.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    @Inject
    lateinit var _passwordless: PasswordlessClient

    @Inject
    lateinit var httpClient: YourBackendHttpClient

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            try {
                val alias = binding.aliasEditText.text.toString()

                _passwordless.login(alias) { success, exception, result ->
                    if (success) {
                        lifecycleScope.launch {
                            val clientDataResponse =
                                httpClient.login(UserLoginRequest(result?.token!!))
                            if (clientDataResponse.isSuccessful) {
                                val data = clientDataResponse.body()
                                val jwt = JWT(data!!.jwtToken)
                                if (jwt.isExpired(60)) {
                                    Toast.makeText(
                                        context,
                                        "Token is expired. Please try again.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    findNavController().navigate(R.id.show_credentials)
                                }
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Exception: " + (exception as Exception).message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        binding.registerNavTV.setOnClickListener {
            findNavController().navigate(R.id.action_to_registration_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}