@file:JvmName("Utils")

package org.poseidonplugins.commandapi

import org.bukkit.command.CommandSender

fun hasPermission(sender: CommandSender, permission: String): Boolean =
    sender.isOp || sender.hasPermission(permission) || permission == ""

fun sendMessage(sender: CommandSender, message: String) =
    sender.sendMessage(message.replace("&([0-9a-f])".toRegex(), "§$1"))

fun getField(obj: Any, name: String): Any {
    val field = obj.javaClass.getDeclaredField(name)
    field.isAccessible = true
    val objField = field.get(obj)
    field.isAccessible = false
    return objField
}