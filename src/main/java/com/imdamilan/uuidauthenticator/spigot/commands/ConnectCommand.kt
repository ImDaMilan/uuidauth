package com.imdamilan.uuidauthenticator.spigot.commands

import com.imdamilan.uuidauthenticator.spigot.UUIDAuthenticator
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ConnectCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val sql = UUIDAuthenticator.getSQL()
        if (sql.isConnected()) {
            sql.disconnect()
            try { sql.connect() } catch (e: Exception) { sender.sendMessage("Failed to connect to the database! Please check your configuration and if your database is up.") }
            sender.sendMessage("Reconnected to the database! Authentication will now work.")
        } else {
            try { sql.connect() } catch (e: Exception) { sender.sendMessage("Failed to connect to the database! Please check your configuration and if your database is up.") }
            sender.sendMessage("Connected to the database! Authentication is now enabled!")
        }
        return true
    }
}
