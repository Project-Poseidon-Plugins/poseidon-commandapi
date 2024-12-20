package org.poseidonplugins.commandapi

import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

/**
 * Stores all relevant information of a command.
 * This can either be a base command or a subcommand, depending on the command's name.
 * For example, **"cmd"** is a base command, **"cmd.help"** is a subcommand.
 *
 * @param name The command's name
 * @param aliases The command's aliases
 * @param description A description of the command
 * @param usage An explanation of how the command is used
 * @param permission The permission needed to execute the command
 * @param isPlayerOnly If the command should only be executed by players
 * @param minArgs How many arguments are needed at least
 * @param maxArgs How many arguments are needed at most
 * @param preprocessor The [Preprocessor] that should process the command
 */
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

    /**
     * Called when the command has been processed and is ready for execution.
     *
     * @param event The [CommandEvent] associated with the execution
     */
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