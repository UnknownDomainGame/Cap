package nullengine.command.util.node;

import nullengine.command.CommandSender;
import nullengine.command.argument.Argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class MultiArgumentNode extends CommandNode {

    private CommandNode commandNode;
    private Function<Object[], Object> instanceFunction;
    private int argsNum;

    public MultiArgumentNode(CommandNode commandNode, Function<Object[], Object> instanceFunction, int argsNum) {
        this.commandNode = commandNode;
        this.instanceFunction = instanceFunction;
        this.argsNum = argsNum;
    }

    @Override
    public List<Object> collect() {
        ArrayList list = new ArrayList();

        MultiInstance instance = getMultiArgInstance();

        list.add(instance.instance);

        if (instance.parent != null)
            list.addAll(instance.parent.collect());

        return list;
    }

    private MultiInstance getMultiArgInstance() {
        ArrayList<Object> args = new ArrayList<>();
        CommandNode parent = this;
        for (int i = 0; i < this.argsNum+getNeedArgs(); i++) {
            if (parent.parseResult == null)
                i--;
            else {
                args.add(parent.parseResult);
                parent.parseResult = null;
                parent = parent.getParent();
            }
        }
        Collections.reverse(args);
        return new MultiInstance(parent, instanceFunction.apply(args.toArray(new Object[0])));
    }


    @Override
    public int getNeedArgs() {
        return commandNode.getNeedArgs();
    }

    @Override
    protected Object parseArgs(CommandSender sender, String command, String... args) {
        return commandNode.parseArgs(sender,command,args);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiArgumentNode that = (MultiArgumentNode) o;
        return argsNum == that.argsNum &&
                Objects.equals(commandNode, that.commandNode) &&
                Objects.equals(instanceFunction, that.instanceFunction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandNode, instanceFunction, argsNum);
    }

    private class MultiInstance {
        public final CommandNode parent;
        public final Object instance;

        public MultiInstance(CommandNode parent, Object instance) {
            this.parent = parent;
            this.instance = instance;
        }
    }

    @Override
    public String toString() {
        return "MultiArgumentNode{" +
                "commandNode=" + commandNode +
                ", instanceFunction=" + instanceFunction +
                ", argsNum=" + argsNum +
                '}';
    }
}
