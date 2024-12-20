package org.poseidonplugins.commandapi

/**
 * Processes the execution of a command.
 */
abstract class Preprocessor {

    /**
     * Processes the arguments to select the command to process.
     * If the first argument matches a subcommand, the subcommand is processed,
     * otherwise the parent command is processed.
     *
     * @param event The [CommandEvent] associated with the execution
     */
    fun invoke(event: CommandEvent) {
        val child: Command? = if (event.args.isNotEmpty()) event.command.getChild(event.args[0]) else null
        if (child != null) {
            val cmdEvent = CommandEvent(event.sender, child, event.label,
                if (event.args.size == 1) listOf() else event.args.subList(1, event.args.size), event.fullCommand)
            child.preprocessor.invoke(cmdEvent)
        } else {
            preprocess(event)
        }
    }

    /**
     * Processes the command.
     *
     * @param event The [CommandEvent] associated with the execution
     */
    abstract fun preprocess(event: CommandEvent)
}