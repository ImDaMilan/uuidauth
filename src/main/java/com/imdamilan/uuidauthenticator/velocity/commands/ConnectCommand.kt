package com.imdamilan.uuidauthenticator.velocity.commands

import com.imdamilan.uuidauthenticator.velocity.UUIDAuthVelocity
import com.velocitypowered.api.command.SimpleCommand
import net.kyori.adventure.text.Component

class ConnectCommand : SimpleCommand {
    override fun execute(invocation: SimpleCommand.Invocation?) {
        val sql = UUIDAuthVelocity.sql
        if (sql!!.isConnected) {
            sql.disconnect()
            try {
                sql.connect()
                invocation?.source()?.sendMessage(Component.text("Reconnected to the database! Authentication will now work."))
            } catch (e: Exception) {
                invocation?.source()?.sendMessage(Component.text("Failed to connect to the database! Please check your configuration and if your database is up."))
            }
        } else {
            try {
                sql.connect()
                invocation?.source()?.sendMessage(Component.text("Connected to the database! Authentication is now enabled!"))
            } catch (e: Exception) {
                invocation?.source()?.sendMessage(Component.text("Failed to connect to the database! Please check your configuration and if your database is up."))
            }
        }
    }
}
