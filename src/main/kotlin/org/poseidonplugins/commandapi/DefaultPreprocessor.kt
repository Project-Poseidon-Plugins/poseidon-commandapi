package org.poseidonplugins.commandapi

import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * The default preprocessor used to process commands.
 * Custom implementations must inherit from [Preprocessor].
 */
class DefaultPreprocessor : Preprocessor() {

    /**
     * Processes the command and executes it when all conditions are met.
     *
     * @param event The [CommandEvent] associated with the execution
     */
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
            Bukkit.getPluginManager().callEvent(event)
            if (!event.isCancelled) event.command.execute(event)
        }
    }
}