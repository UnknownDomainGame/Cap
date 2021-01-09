package engine.command.util.node;

import engine.command.util.StringArgs;
import engine.command.util.context.ContextNode;
import engine.command.util.context.LinkedContext;

import java.util.ArrayList;
import java.util.List;
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
    public int getRequiredArgsNum() {
        return commandNode.getRequiredArgsNum();
    }

    @Override
    public Object parse(LinkedContext sender, StringArgs args) {
        return commandNode.parse(sender, args);
    }

    @Override
    public void collect(ContextNode node) {
        List<Object> objectList = new ArrayList<>();
        ContextNode headNode = node;
        ContextNode pre = headNode.getPre();
        for (int i = 0; i < argsNum; i++) {
            headNode = headNode.getPre();
        }
        for (int i = 0; i < argsNum; i++) {
            objectList.add(headNode.getValue());
            headNode = headNode.getNext();
        }
        node.setValue(instanceFunction.apply(objectList.toArray()));
        pre.setNext(node);
        node.setPre(pre);
    }

    @Override
    public String getTip() {
        return commandNode.getTip();
    }

    @Override
    public void setTip(String tip) {
        commandNode.setTip(tip);
    }

    @Override
    public boolean hasTip() {
        return commandNode.hasTip();
    }

    @Override
    public String toString() {
        return "MultiArgumentNode{" +
                "commandNode=" + commandNode +
                ", instanceFunction=" + instanceFunction +
                ", argsNum=" + argsNum +
                '}';
    }

    @Override
    public int priority() {
        return commandNode.priority();
    }

    @Override
    public boolean same(CommandNode node) {
        if (super.same(node) && node instanceof MultiArgumentNode) {
            MultiArgumentNode multiArgumentNode = (MultiArgumentNode) node;
            return argsNum == multiArgumentNode.argsNum &&
                    commandNode.same(multiArgumentNode.commandNode) &&
                    instanceFunction.equals(multiArgumentNode.instanceFunction);
        }
        return false;
    }
}
