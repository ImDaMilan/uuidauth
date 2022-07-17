package com.imdamilan.uuidauthenticator.velocity.commands

import com.imdamilan.uuidauthenticator.velocity.UUIDAuthVelocity
import com.velocitypowered.api.command.SimpleCommand
import net.kyori.adventure.text.Component

class DisconnectCommand : SimpleCommand {

    override fun execute(invocation: SimpleCommand.Invocation?) {
        val sql = UUIDAuthVelocity.sql
        if (sql != null) {
            if (sql.isConnected) {
                sql.disconnect()
                invocation?.source()?.sendMessage(Component.text("Disconnected from database!"))
            } else {
                invocation?.source()?.sendMessage(Component.text("Not connected to the database!"))
            }
        }
    }
}
