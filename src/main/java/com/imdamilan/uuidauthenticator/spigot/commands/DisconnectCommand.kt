package com.imdamilan.uuidauthenticator.spigot.commands

import com.imdamilan.uuidauthenticator.spigot.UUIDAuthenticator
import org.bukkit.command.CommandExecutor

class DisconnectCommand : CommandExecutor {
    override fun onCommand(
        sender: org.bukkit.command.CommandSender,
        command: org.bukkit.command.Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        val sql = UUIDAuthenticator.getSQL()
        return if (sql.isConnected()) {
            sql.disconnect()
            sender.sendMessage("Disconnected from database!")
            true
        } else {
            sender.sendMessage("Not connected to database!")
            false
        }
    }
}
