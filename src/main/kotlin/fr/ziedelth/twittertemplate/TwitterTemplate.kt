package fr.ziedelth.twittertemplate

import com.google.gson.GsonBuilder
import fr.ziedelth.utils.plugins.JaisPlugin
import org.pf4j.PluginWrapper
import twitter4j.Twitter
import java.io.File

class TwitterTemplate(wrapper: PluginWrapper) : JaisPlugin(wrapper) {
    private var twitter: Twitter? = null

    override fun start() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        val file = File(dataFolder, "config.json")
        val gson = GsonBuilder().setPrettyPrinting().create()

        if (!file.exists()) {
            println("Creating config file...")
            file.createNewFile()
            file.writeText(gson.toJson(Configuration()))
            throw RuntimeException("Please fill the config file before restarting the plugin.")
        }

        val config = gson.fromJson(file.readText(), Configuration::class.java)

        if (!config.isValid()) {
            throw RuntimeException("Please fill the config file before restarting the plugin.")
        }

        println("Starting TwitterTemplate...")

        try {
            twitter = Twitter.newBuilder().oAuthConsumer(config.oAuthConsumerKey, config.oAuthConsumerSecret).oAuthAccessToken(config.oAuthAccessToken, config.oAuthAccessTokenSecret).build()
            // Test the connection
            twitter?.v1()?.tweets()?.lookup(1)
        } catch (e: Exception) {
            throw RuntimeException("An error occurred while creating the Twitter instance. Please check your credentials.")
        }
    }
}