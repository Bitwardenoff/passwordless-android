package dev.passwordless.android.rest

/**
 * @property apiUrl The Passwordless.dev server url.
 * @property apiKey Your public API key.
 * @property rpId This stands for “relying party”; it can be considered as describing the organization responsible for registering and authenticating the user.
 * @property origin This is where your backend is hosted.
 * @property
 */
data class PasswordlessOptions(
    val apiKey: String,
    val rpId: String,
    val origin: String,
    val backendUrl:String,
    val apiUrl: String = "https://v4.passwordless.dev"
)
