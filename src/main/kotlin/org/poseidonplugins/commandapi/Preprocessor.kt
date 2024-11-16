package org.poseidonplugins.commandapi

abstract class Preprocessor {
    fun invoke(event: CommandEvent) {
        val child: Command? = if (event.args.isNotEmpty()) event.command.getChild(event.args[0]) else null
        if (child != null) {
            val cmdEvent = CommandEvent(event.sender, child, event.label,
                if (event.args.size == 1) listOf() else event.args.subList(1, event.args.size))
            child.preprocessor.invoke(cmdEvent)
        } else {
            preprocess(event)
        }
    }

    abstract fun preprocess(event: CommandEvent)
}