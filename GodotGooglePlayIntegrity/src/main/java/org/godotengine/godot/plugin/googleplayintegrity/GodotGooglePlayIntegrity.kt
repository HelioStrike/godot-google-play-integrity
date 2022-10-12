package org.godotengine.godot.plugin.googleplayintegrity

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.play.core.integrity.IntegrityManager
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.android.play.core.integrity.IntegrityTokenResponse
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo

class GodotGooglePlayIntegrity : GodotPlugin {
    private val integrityManager: IntegrityManager = IntegrityManagerFactory.create(activity)

    constructor(godot: Godot) : super(godot)

    override fun getPluginName() = "GodotGooglePlayIntegrity"

    fun requestIntegrityToken(cloudProjectNumber: String, nonce: String) {
        Log.d(pluginName, "Requesting Play Integrity token. Nonce=$nonce")

        val integrityTokenResponse: Task<IntegrityTokenResponse> =
            integrityManager.requestIntegrityToken(
                IntegrityTokenRequest.builder()
                    .setCloudProjectNumber(cloudProjectNumber.toLong())
                    .setNonce(nonce).build()
            )

        integrityTokenResponse.addOnCompleteListener { response ->
            Log.d(pluginName, "Received token: ${response.result.token()}")
            emitSignal("integrity_token_received", response.result.token())
        }
    }

    override fun getPluginMethods(): MutableList<String> {
        return mutableListOf("requestIntegrityToken")
    }

    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return mutableSetOf<SignalInfo>(SignalInfo("integrity_token_received", String::class.java))
    }
}
