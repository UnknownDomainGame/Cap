package nullengine.command.argument.base;


import com.google.common.collect.Lists;
import nullengine.command.argument.SimpleArgument;
import nullengine.command.suggestion.Suggester;

import java.util.Optional;

public class IntegerArgument extends SimpleArgument {

    public IntegerArgument() {
        super(Integer.class,"Integer");
    }

    @Override
    public Optional parse(String arg) {
        try {
            return Optional.of(Integer.valueOf(arg));
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Suggester getSuggester() {
        return (sender, command, args) -> Lists.newArrayList("[num]");
    }
}
