package main;

import engine.command.anno.Provide;
import engine.command.anno.Sender;
import engine.command.anno.Tip;

public class LocationProvider {

    @Provide
    public Location a(@Sender Entity entity, @Tip("x") double x, @Tip("y") double y, @Tip("z") double z){
        return b(entity.getWorld(),x,y,z);
    }

    @Provide
    public Location b(@Tip("world") World world,@Tip("x") double x,@Tip("y") double y,@Tip("z") double z){
        return new Location(world,x,y,z);
    }

}
