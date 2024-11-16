package org.poseidonplugins.commandapi

import org.bukkit.command.CommandSender
import org.bukkit.event.Event

class CommandEvent(
    val sender: CommandSender,
    val command: Command,
    val label: String,
    val args: List<String>
) : Event("CommandEvent")