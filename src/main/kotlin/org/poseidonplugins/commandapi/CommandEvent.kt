package org.poseidonplugins.commandapi

import org.bukkit.command.CommandSender
import org.bukkit.event.Cancellable
import org.bukkit.event.Event

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