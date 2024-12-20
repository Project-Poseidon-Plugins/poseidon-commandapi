package org.poseidonplugins.commandapi

import org.bukkit.command.CommandSender
import org.bukkit.event.Cancellable
import org.bukkit.event.Event

/**
 * Stores relevant information about the execution of a command.
 *
 * @param sender The [CommandSender] that executed the command
 * @param command The command that was executed
 * @param label The command label that was used
 * @param args The arguments that were provided
 * @param fullCommand The full command string that was typed
 */
class CommandEvent(
    val sender: CommandSender,
    val command: Command,
    val label: String,
    val args: List<String>,
    val fullCommand: String,
) : Event("CommandEvent"), Cancellable {

    private var isCancelled = false

    override fun isCancelled(): Boolean = isCancelled
    override fun setCancelled(cancel: Boolean) { isCancelled = cancel }
}