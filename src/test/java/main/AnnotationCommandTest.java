package main;

import org.junit.Assert;
import org.junit.Test;
import unknowndomain.command.CommandResult;
import unknowndomain.command.CommandSender;
import unknowndomain.command.anno.AnnotationCommand;
import unknowndomain.command.anno.node.ArgumentNode;
import unknowndomain.command.anno.node.CommandNode;
import unknowndomain.command.anno.node.SenderNode;
import unknowndomain.command.argument.IntegerArgument;
import unknowndomain.command.argument.StringArgument;
import unknowndomain.command.exception.CommandSenderErrorException;
import unknowndomain.permission.HashPermissible;
import unknowndomain.permission.Permissible;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class AnnotationCommandTest {

    CommandSender sender = new TestSender();

    @Test
    public void Test1() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {

        Constructor constructor = AnnotationCommand.class.getDeclaredConstructor(String.class,String.class,String.class);
        constructor.setAccessible(true);

        Field field = AnnotationCommand.class.getDeclaredField("annotationNode");

        field.setAccessible(true);

        AnnotationCommand annotationCommand = (AnnotationCommand) constructor.newInstance("test","description","helpMessage");

        CommandNode commandNode = (CommandNode) field.get(annotationCommand);

        CommandNode node = new ArgumentNode(new StringArgument());
        node.setInstance(this);
        node.setMethod(getClass().getMethod("test",String.class));
        commandNode.addChild(node);

        CommandNode node2 = new ArgumentNode(new IntegerArgument());
        CommandNode node3 = new ArgumentNode(new StringArgument());
        node2.addChild(node3);

        node3.setInstance(this);
        node3.setMethod(getClass().getMethod("test2", int.class, String.class));

        commandNode.addChild(node2);

        annotationCommand.execute(sender,new String[]{"zsd"});
        annotationCommand.execute(sender,new String[]{"2","asd"});

    }

    public void test(String a){
        System.out.println(a);
    }

    public void test2(int value,String text){
        System.out.println(value+" -- "+text);

    }

    @Test
    public void senderTest() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Constructor constructor = AnnotationCommand.class.getDeclaredConstructor(String.class,String.class,String.class);
        constructor.setAccessible(true);

        Field field = AnnotationCommand.class.getDeclaredField("annotationNode");

        field.setAccessible(true);

        AnnotationCommand annotationCommand = (AnnotationCommand) constructor.newInstance("test","description","helpMessage");

        CommandNode commandNode = (CommandNode) field.get(annotationCommand);

        CommandNode senderNode = new SenderNode(TestSender.class);
        CommandNode stringNode = new ArgumentNode(new StringArgument());
        senderNode.addChild(stringNode);

        stringNode.setInstance(this);
        stringNode.setMethod(getClass().getMethod("senderTest", CommandSender.class, String.class));

        commandNode.addChild(senderNode);

        CommandResult result = annotationCommand.execute(new TestSender2(),new String[]{"abc"});
        CommandResult result1 = annotationCommand.execute(sender,new String[]{"abc"});

        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result1.isSuccess());

        Assert.assertEquals(result1.getMessage(),sender.getSenderName()+" --- "+"abc");
        Assert.assertEquals(result.getCause().getClass(), CommandSenderErrorException.class);
    }

    public CommandResult senderTest(CommandSender sender,String text){
        return new CommandResult(true,sender.getSenderName()+" --- "+text);
    }


    class Location {

        int x;
        int y;
        int z;

        public Location(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString() {
            return "Location{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }
    }

    private class TestSender implements CommandSender{
        private HashPermissible hashPermissible = new HashPermissible();
        @Override
        public void sendMessage(String message) {

        }

        @Override
        public String getSenderName() {
            return "test sender";
        }

        @Override
        public Permissible getPermissible() {
            return hashPermissible;
        }
    }

    private class TestSender2 implements CommandSender{
        private HashPermissible hashPermissible = new HashPermissible();
        @Override
        public void sendMessage(String message) {

        }

        @Override
        public String getSenderName() {
            return "test2 sender";
        }

        @Override
        public Permissible getPermissible() {
            return hashPermissible;
        }
    }


}
