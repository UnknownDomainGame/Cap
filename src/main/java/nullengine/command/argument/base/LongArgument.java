package nullengine.command.argument.base;

import com.google.common.collect.Lists;
import nullengine.command.argument.SimpleArgument;
import nullengine.command.suggestion.Suggester;

import java.util.Optional;

public class LongArgument extends SimpleArgument {
    public LongArgument() {
        super(Long.class,"Long");
    }

    @Override
    public Optional parse(String arg) {
        try {
            return Optional.of(Long.valueOf(arg));
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Suggester getSuggester() {
        return (sender, command, args) -> Lists.newArrayList("[num]");
    }
}
