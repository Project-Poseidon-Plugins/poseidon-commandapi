@file:JvmName("Utils")

package org.poseidonplugins.commandapi

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap

fun hasPermission(sender: CommandSender, permission: String): Boolean =
    sender.isOp || sender.hasPermission(permission) || permission == ""

fun sendMessage(sender: CommandSender, message: String) = sender.sendMessage(colorize(message))

fun sendMessage(sender: CommandSender, obj: Any) = sendMessage(sender, obj.toString())

fun colorize(message: String) = message.replace("&([0-9a-f])".toRegex(), "§$1")

fun getField(obj: Any, name: String): Any {
    val field = obj.javaClass.getDeclaredField(name)
    field.isAccessible = true
    val objField = field.get(obj)
    field.isAccessible = false
    return objField
}

fun getCommandMap(): CommandMap = getField(Bukkit.getPluginManager(), "commandMap") as SimpleCommandMap

fun getPluginCommands(): Map<String, Command> {
    var commands = getField(getCommandMap(), "knownCommands") as Map<String, Command>

    return commands
        .filter { (name, command) -> command is PluginCommand && name == command.name }
        .toSortedMap(compareBy<String> { (commands[it] as PluginCommand).plugin.description.name }.thenBy { it })
}