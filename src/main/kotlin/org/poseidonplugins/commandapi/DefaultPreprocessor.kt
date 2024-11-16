package org.poseidonplugins.commandapi

import org.bukkit.entity.Player

class DefaultPreprocessor : Preprocessor() {
    override fun preprocess(event: CommandEvent) {
        if (!hasPermission(event.sender, event.command.permission)) {
            sendMessage(event.sender, "&cYou don't have permission to do that.")
        } else if (event.command.isPlayerOnly && event.sender !is Player) {
            sendMessage(event.sender, "&cOnly players can run this command.")
        } else if (event.args.size < event.command.minArgs) {
            sendMessage(event.sender, "&cYou have not provided enough arguments.")
            sendMessage(event.sender, "&cUsage: ${event.command.usage}")
        } else if (event.args.size > event.command.maxArgs && event.command.maxArgs >= 0) {
            sendMessage(event.sender, "&cYou have provided too many arguments.")
            sendMessage(event.sender, "&cUsage: ${event.command.usage}")
        } else {
            event.command.execute(event)
        }
    }
}