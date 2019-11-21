package nullengine.command;

import nullengine.command.exception.CommandNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseCommandManager implements CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final UncaughtExceptionHandler uncaughtExceptionHandler = createUncaughtExceptionHandler();
    private final CommandResolver resolver = createCommandResolver();

    protected abstract UncaughtExceptionHandler createUncaughtExceptionHandler();

    protected abstract CommandResolver createCommandResolver();

    @Override
    public void registerCommand(Command command) {
        if (commands.containsKey(command.getName()))
            throw new RuntimeException("Command \"" + command.getName() + "\" already exists");
        commands.put(command.getName(), command);
    }

    @Override
    public Optional<Command> getCommand(String command) {
        return Optional.ofNullable(commands.get(command));
    }

    @Override
    public boolean hasCommand(String command) {
        return commands.containsKey(command);
    }

    @Override
    public void execute(CommandSender sender, String rawCommand) {
        CommandResolver.Result result = resolver.resolve(rawCommand);
        execute(sender, result.command, result.args);
    }

    @Override
    public void execute(CommandSender sender, String command, String... args) {
        Command commandInstance = commands.get(command);
        if (commandInstance == null) {
            throw new CommandNotFoundException(command);
        }

        if (args == null) {
            args = new String[0];
        }
        try {
            commandInstance.execute(sender, args);
        } catch (Exception e) {
            if (commandInstance.handleUncaughtException(e, sender, args)) {
                return;
            }
            uncaughtExceptionHandler.handle(e, sender, commandInstance, args);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String rawCommand) {
        CommandResolver.Result result = resolver.resolve(rawCommand);
        return complete(sender, result.command, result.args);
    }

    @Override
    public List<String> complete(CommandSender sender, String command, String... args) {
        Command commandInstance = commands.get(command);
        if ((args == null || args.length == 0) && commandInstance == null) {
            return commands.keySet()
                    .stream()
                    .filter(commandName -> commandName.startsWith(command)).collect(Collectors.toList());
        }

        if (commandInstance == null)
            return Collections.EMPTY_LIST;

        return commandInstance.suggest(sender, args);
    }

    @Override
    public List<String> getTips(CommandSender sender, String rawCommand) {
        CommandResolver.Result result = resolver.resolve(rawCommand);
        return getTips(sender, result.command, result.args);
    }

    @Override
    public List<String> getTips(CommandSender sender, String command, String... args) {
        if (command == null || command.isEmpty())
            return Collections.EMPTY_LIST;
        Command commandInstance = commands.get(command);
        if (commandInstance == null)
            return Collections.EMPTY_LIST;
        return commandInstance.getTips(sender, args);
    }

    @Override
    public ArgumentCheckResult checkLastArgument(CommandSender sender, String rawCommand) {
        CommandResolver.Result result = this.resolver.resolve(rawCommand);
        return checkLastArgument(sender, result.command, result.args);
    }

    @Override
    public ArgumentCheckResult checkLastArgument(CommandSender sender, String command, String... args) {
        Command command1 = commands.get(command);
        if (command1 == null)
            return ArgumentCheckResult.Error("/" + command + "  command not found");
        return command1.checkLastArgument(sender, args);
    }

    @Override
    public void unregisterCommand(String command) {
        commands.remove(command);
    }
}