package unknowndomain.command.exception;

import unknowndomain.command.CommandSender;

public class CommandSenderErrorException extends CommandException {

    private final CommandSender commandSender;

    public CommandSenderErrorException(String command, CommandSender commandSender) {
        super(command);
        this.commandSender = commandSender;
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }
}
