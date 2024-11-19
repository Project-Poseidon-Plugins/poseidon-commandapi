package org.poseidonplugins.commandapi

import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

abstract class Command(
    name: String,
    aliases: List<String> = listOf(),
    val description: String = "",
    val usage: String,
    val permission: String = "",
    val isPlayerOnly: Boolean = false,
    val minArgs: Int = 0,
    val maxArgs: Int = -1,
    val preprocessor: Preprocessor = DefaultPreprocessor()
) : CommandExecutor {

    val name: String = name.lowercase()
    val aliases: List<String> = aliases.map { it.lowercase() }
    private var parent: Command? = null
    private val childCommands: MutableMap<String, Command> = mutableMapOf()

    abstract fun execute(event: CommandEvent)

    final override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
        preprocessor.invoke(CommandEvent(sender, this, label, args.toList(), "/$label ${args.joinToString(" ")}"))
        return true
    }

    fun getParent(): Command? = parent

    fun addChild(name: String, child: Command) {
        childCommands[name.lowercase()] = child
        child.parent = this
    }

    fun getChildren(): Map<String, Command> = childCommands.toMap()

    fun getChild(name: String): Command? {
        for (child in childCommands.values) {
            if (child.getLabels().contains(name.lowercase())) return child
        }
        return null
    }

    fun hasChild(name: String): Boolean = childCommands.containsKey(name.lowercase())

    fun getLabels(): List<String> {
        val list = name.split(".")
        return listOf(list[list.size - 1]).plus(aliases)
    }

}