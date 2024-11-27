@file:JvmName("Utils")

package org.poseidonplugins.commandapi

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap
import java.util.SortedMap

fun hasPermission(sender: CommandSender, permission: String?): Boolean =
    permission.isNullOrEmpty() || sender.isOp || sender.hasPermission(permission)

fun sendMessage(sender: CommandSender, message: String) = sender.sendMessage(colorize(message))

fun sendMessage(sender: CommandSender, obj: Any) = sendMessage(sender, obj.toString())

fun broadcastMessage(message: String) = Bukkit.broadcastMessage(colorize(message))

fun broadcastMessage(obj: Any) = broadcastMessage(obj.toString())

fun broadcastMessage(message: String, permission: String) {
    for (player in Bukkit.getOnlinePlayers()) {
        if (hasPermission(player, permission)) sendMessage(player, message)
    }
}

fun broadcastMessage(obj: Any, permission: String) = broadcastMessage(obj.toString(), permission)

fun colorize(message: String) = message.replace("&([0-9a-f])".toRegex(), "§$1")

fun joinArgs(list: List<Any>, fromIndex: Int, toIndex: Int, delimiter: String) =
    list.subList(fromIndex, toIndex).joinToString(delimiter)

fun joinArgs(list: List<Any>, fromIndex: Int, toIndex: Int) = joinArgs(list, fromIndex, toIndex, " ")

fun joinArgs(list: List<Any>, fromIndex: Int, delimiter: String) = joinArgs(list, fromIndex, list.size, delimiter)

fun joinArgs(list: List<Any>, fromIndex: Int) = joinArgs(list, fromIndex, list.size)

fun getField(obj: Any, name: String): Any {
    val field = obj.javaClass.getDeclaredField(name)
    field.isAccessible = true
    val objField = field.get(obj)
    field.isAccessible = false
    return objField
}

fun getCommandMap(): CommandMap = getField(Bukkit.getPluginManager(), "commandMap") as SimpleCommandMap

fun getPluginCommands(): Map<String, PluginCommand> {
    val commands = getField(getCommandMap(), "knownCommands") as Map<String, Command>

    return commands
        .filter { (name, command) -> command is PluginCommand && name == command.name }
        .toSortedMap(compareBy<String> { (commands[it] as PluginCommand).plugin.description.name }.thenBy { it })
        as SortedMap<String, PluginCommand>
}