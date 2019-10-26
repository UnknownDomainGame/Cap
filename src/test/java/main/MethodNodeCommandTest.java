package main;

import nullengine.command.CommandSender;
import nullengine.command.SimpleCommandManager;
import nullengine.command.anno.*;
import nullengine.command.argument.Argument;
import nullengine.command.argument.ArgumentManager;
import nullengine.command.argument.SimpleArgumentManager;
import nullengine.command.completion.CompleteManager;
import nullengine.command.completion.Completer;
import nullengine.command.completion.NamedCompleter;
import nullengine.command.completion.SimpleCompleteManager;
import nullengine.command.exception.CommandWrongUseException;
import nullengine.command.exception.PermissionNotEnoughException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;


public class MethodNodeCommandTest {

    private TestSender testSender = new TestSender("methodNodeTest", string -> message = string);

    private String message;

    SimpleCommandManager simpleCommandManager = new SimpleCommandManager();

    public MethodNodeCommandTest() {
        MethodAnnotationCommand.getBuilder(simpleCommandManager)
                .addCommandHandler(this)
                .register();
    }

    @Test
    public void commandAttributeTest() {
        nullengine.command.Command command = simpleCommandManager.getCommand("command").get();

        Assertions.assertEquals(command.getDescription(), "desc");
        Assertions.assertEquals(command.getHelpMessage(), "helpMessage");

        simpleCommandManager.execute(testSender, "command");
        Assertions.assertEquals(message, "command");
    }

    @Command(value = "command", desc = "desc", helpMessage = "helpMessage")
    public void command() {
        message = "command";
    }

    @Test
    void permissionTest() {
        testSender.removePermission("permission.use");
        testSender.removePermission("permission");
        testSender.removePermission("op");
        nullengine.command.Command command = simpleCommandManager.getCommand("permission").get();
        Assertions.assertThrows(PermissionNotEnoughException.class, () -> command.execute(testSender, new String[0]));
        testSender.setPermission("permission", true);
        Assertions.assertDoesNotThrow(() -> command.execute(testSender, new String[0]));
        testSender.setPermission("permission", false);
        testSender.setPermission("permission.use", true);
        Assertions.assertDoesNotThrow(() -> command.execute(testSender, new String[0]));
        testSender.removePermission("permission.use");
        testSender.removePermission("permission");
        Assertions.assertEquals(message, "p");
    }

    @Command(value = "permission")
    @Permission({"permission.use"})
    public void permission() {
        message = "p";
    }

    @Test
    public void senderTest() throws Exception {
        nullengine.command.Command command = simpleCommandManager.getCommand("sender").get();
        Sender1 sender1 = new Sender1();
        command.execute(sender1, new String[0]);
        Assertions.assertEquals(message, sender1.getSenderName());
        Sender2 sender2 = new Sender2();
        command.execute(sender2, new String[0]);
        Assertions.assertEquals(message, sender2.getSenderName());
        Assertions.assertThrows(CommandWrongUseException.class, () -> command.execute(testSender, new String[0]));
    }

    @Command("sender")
    public void sender(@Sender({Sender1.class, Sender2.class}) CommandSender sender) {
        message = sender.getSenderName();
    }

    private class Sender1 extends TestSender {

        public Sender1() {
            super("testSender1", s -> {
            });
        }
    }

    private class Sender2 implements CommandSender {
        @Override
        public void sendMessage(String message) {
        }

        @Override
        public String getSenderName() {
            return "sender2";
        }

        @Override
        public boolean hasPermission(String permission) {
            return true;
        }

        @Override
        public void setPermission(String permission, boolean bool) {
        }

        @Override
        public void removePermission(String permission) {
        }
    }

    @Test
    public void argumentTest() {

        SimpleCommandManager simpleCommandManager = new SimpleCommandManager();
        ArgumentManager argumentManager = new SimpleArgumentManager();
        argumentManager.appendArgument(new Argument() {
            @Override
            public String getName() {
                return "testArgument";
            }

            @Override
            public Class responsibleClass() {
                return String.class;
            }

            @Override
            public Optional parse(String arg) {
                return Optional.of("test" + arg);
            }

            @Override
            public Completer getCompleter() {
                return ((sender, command, args) -> Collections.EMPTY_LIST);
            }
        });

        argumentManager.setClassDefaultArgument(new Argument() {
            @Override
            public String getName() {
                return "random";
            }

            @Override
            public Class responsibleClass() {
                return Random.class;
            }

            @Override
            public Optional parse(String arg) {
                return Optional.of(new Random(Integer.valueOf(arg)));
            }

            @Override
            public Completer getCompleter() {
                return (sender, command, args) -> Collections.EMPTY_LIST;
            }
        });

        MethodAnnotationCommand.getBuilder(simpleCommandManager)
                .setArgumentManager(argumentManager)
                .addCommandHandler(new ArgumentTestClass())
                .register();

        int randomSeed = 12314;

        simpleCommandManager.execute(testSender, "argument " + randomSeed + " argument");

        Random random = new Random(randomSeed);
        Assertions.assertEquals(message, random.nextInt() + "testargument");
    }

    public class ArgumentTestClass {
        @Command("argument")
        public void argument(Random random, @ArgumentHandler("testArgument") String argumentMessage) {
            message = Integer.valueOf(random.nextInt()).toString() + argumentMessage;
        }
    }

    @Test
    void completeTest() {

        SimpleCommandManager simpleCommandManager = new SimpleCommandManager();
        CompleteManager completeManager = new SimpleCompleteManager();
        completeManager.putCompleter(new NamedCompleter() {
            @Override
            public String getName() {
                return "completeTest";
            }

            @Override
            public List<String> complete(CommandSender sender, String command, String[] args) {
                return List.of("test");
            }
        });

        MethodAnnotationCommand.getBuilder(simpleCommandManager)
                .addCommandHandler(new CompleteTestClass())
                .setCompleteManager(completeManager)
                .register();

        List<String> completeResult = simpleCommandManager.complete(testSender, "complete ");
        Assertions.assertEquals(1, completeResult.size());
        Assertions.assertEquals("test", completeResult.get(0));
    }

    public class CompleteTestClass {
        @Command("complete")
        public void complete(@nullengine.command.anno.Completer("completeTest") String a) {
        }
    }

    @Test
    public void requiredTest() throws Exception {
        nullengine.command.Command command = simpleCommandManager.getCommand("required").get();

        Assertions.assertThrows(CommandWrongUseException.class, () -> command.execute(testSender, new String[]{"c"}));

        command.execute(testSender, new String[]{"a"});
        Assertions.assertEquals(message, "a");
        command.execute(testSender, new String[]{"b"});
        Assertions.assertEquals(message, "b");
    }

    @Command("required")
    public void required1(@Required("a") String a) {
        message = a;
    }

    @Command("required")
    public void required2(@Required("b") String a) {
        message = a;
    }

    @Test
    void tip() {
        List<String> tips = simpleCommandManager.getTips(testSender, "tip ");
        Assertions.assertArrayEquals(tips.toArray(),new String[]{"x","y","z"});
        tips = simpleCommandManager.getTips(testSender, "tip 2");
        Assertions.assertArrayEquals(tips.toArray(),new String[]{"x","y","z"});
        tips = simpleCommandManager.getTips(testSender, "tip 2 5");
        Assertions.assertArrayEquals(tips.toArray(),new String[]{"y","z"});
        tips = simpleCommandManager.getTips(testSender, "tip 2 5 6");
        Assertions.assertArrayEquals(tips.toArray(),new String[]{"z"});
    }

    @Command("tip")
    public void tip(@Tip("x") int x, @Tip("y") int y, @Tip("z") int z) {}

    @Test
    void generator() {
        Entity entitySender = new Entity() {
            @Override
            public String getWorld() {
                return "testWorld";
            }

            @Override
            public void sendMessage(String message) {}

            @Override
            public String getSenderName() {
                return "entity";
            }

            @Override
            public boolean hasPermission(String permission) {
                return true;
            }

            @Override
            public void setPermission(String permission, boolean bool) {}

            @Override
            public void removePermission(String permission) {}
        };

        simpleCommandManager.execute(entitySender,"generator 2 3 4 hell 12 13 14");
        Assertions.assertEquals(message, entitySender.getSenderName()+new Location(entitySender,2,3,4)+new Location("hell",12,13,14));
    }

    @Command("generator")
    public void generatorTest(@Sender CommandSender sender,Location location,Location location2){
        message = sender.getSenderName()+location+location2;
    }

}