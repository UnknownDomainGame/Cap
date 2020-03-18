package engine.command.argument.base;

import com.google.common.collect.Lists;
import engine.command.argument.SimpleArgument;
import engine.command.suggestion.Suggester;

import java.util.Collections;
import java.util.Optional;

public class StringArgument extends SimpleArgument {

    public StringArgument() {
        super(String.class, "String");
    }

    @Override
    public Optional parse(String arg) {
        return Optional.of(arg);
    }

    @Override
    public Suggester getSuggester() {
        return (sender, command, args) -> {
            String s = args[args.length - 1];
            if (s.isEmpty()){
                return Lists.newArrayList("[text]");
            }
            else return Collections.EMPTY_LIST;
        };
    }
}