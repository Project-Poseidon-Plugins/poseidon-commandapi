package org.poseidonplugins.commandapi

import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap
import org.bukkit.plugin.Plugin
import java.lang.reflect.Constructor
import java.util.logging.Logger

class CommandManager(private val plugin: Plugin) {

    private val logger: Logger = Bukkit.getLogger()
    private val prefix: String = "[${plugin.description.name}]"
    private val commandMap: CommandMap

    private val baseCommands: MutableMap<String, Command> = mutableMapOf()
    private val queuedCommands : MutableMap<Int, MutableList<Command>> = mutableMapOf()

    init {
        try {
            val field = Bukkit.getPluginManager().javaClass.getDeclaredField("commandMap")
            field.isAccessible = true
            commandMap = field.get(Bukkit.getPluginManager()) as SimpleCommandMap
            field.isAccessible = false
            logger.info("$prefix Initialized command map")
        } catch (e: Exception) {
            logger.severe("$prefix Error while initializing command map")
            throw e
        }
    }

    fun registerCommands(vararg commands: Command) {
        for (command in commands) {
            if (command.name.contains(".")) {
                queueSubcommand(command)
            } else {
                registerBaseCommand(command)
            }
        }
        registerSubcommands()
    }

    private fun registerBaseCommand(command: Command) {
        try {
            val constructor: Constructor<PluginCommand> = PluginCommand::class.java.getDeclaredConstructor(String::class.java, Plugin::class.java)
            constructor.isAccessible = true
            val pluginCmd = constructor.newInstance(command.name, plugin)
            constructor.isAccessible = false
            pluginCmd.aliases = command.aliases
            pluginCmd.description = command.description
            pluginCmd.usage = command.usage
            pluginCmd.executor = command
            commandMap.register(plugin.description.name, pluginCmd)
            baseCommands[command.name] = command
            logger.info("$prefix Registered command '${command.name}'")
        } catch (e: Exception) {
            logger.severe("$prefix Error while registering command '${command.name}'")
            e.printStackTrace()
        }
    }

    private fun queueSubcommand(command: Command) {
        val level = command.name.split(".").size - 1
        val queue: MutableList<Command> = queuedCommands.getOrDefault(level, mutableListOf())
        if (!queue.contains(command)) {
            queue.add(command)
        }
        queuedCommands[level] = queue
    }

    private fun registerSubcommands() {
        if (queuedCommands.isEmpty()) return
        val highestLevel = queuedCommands.keys.max()
        for (i in 1..highestLevel) {
            val queue = queuedCommands[i]
            if (queue.isNullOrEmpty()) return
            for (command in queue) {
                val list = command.name.split(".")
                val parentCmd: Command? = getParentCommand(list, baseCommands[list[0]], 1)
                if (parentCmd == null) {
                    logger.warning("$prefix Could not register subcommand '${command.name}': Parent command does not exist")
                    continue
                }
                registerChild(parentCmd, list[list.size - 1], command)
            }
        }
    }

    private fun registerChild(parentCmd: Command, name: String, command: Command) {
        when (parentCmd.getChildren().isEmpty()) {
            true -> {
                parentCmd.addChild(name, command)
                logger.info("$prefix Registered subcommand '${command.name}'")
            }

            false -> {
                var add = true
                for (alias in command.getLabels()) {
                    for (childCmd in parentCmd.getChildren()) {
                        if (childCmd.value.getLabels().contains(alias)) {
                            add = false
                            logger.warning("$prefix Could not register subcommand '${command.name}': Label '$alias' is already used by another subcommand of the parent")
                            break
                        }
                    }
                    if (!add) return
                }
                parentCmd.addChild(name, command)
                logger.info("$prefix Registered subcommand '${command.name}'")
            }
        }
    }

    private fun getParentCommand(list: List<String>, command: Command?, start: Int): Command? {
        if (command == null) return null
        if (start > list.size - 2) return command
        return if (command.hasChild(list[start])) getParentCommand(list, command.getChild(list[start]), start + 1) else null
    }
}