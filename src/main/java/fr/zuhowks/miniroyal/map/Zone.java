package fr.zuhowks.miniroyal.map;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import com.sk89q.worldedit.world.World;
import fr.zuhowks.miniroyal.MiniRoyal;
import fr.zuhowks.miniroyal.utils.BukkitUtils;
import org.bukkit.Location;

public class Zone {

    private final double maxRadius;
    private int radius;
    public Zone(MiniRoyalMap miniRoyalMap) {
        final Location pos1 = miniRoyalMap.getPos1();
        final Location pos2 = miniRoyalMap.getPos2();
        final Location center = MiniRoyal.getINSTANCE().getMiniRoyalMap().getFinishZoneCenter();
        final double vecPos1Center = Math.sqrt(Math.pow(pos1.getX()-center.getX(), 2) + Math.pow(pos1.getZ()-center.getZ(), 2));
        final double vecPos2Center = Math.sqrt(Math.pow(pos2.getX()-center.getX(), 2) + Math.pow(pos2.getZ()-center.getZ(), 2));
        this.maxRadius = Math.max(vecPos1Center, vecPos2Center);
        this.radius = (int) (this.maxRadius + 1);
    }
    public void createCylinder() throws MaxChangedBlocksException {
        Location center = MiniRoyal.getINSTANCE().getMiniRoyalMap().getFinishZoneCenter();
        World world = new BukkitWorld(center.getWorld());
        EditSession editSession = BukkitUtils.getEditSessionFromWorldEdit(world);
        editSession.enableQueue();
        editSession.makeCylinder(new Vector(center.getX(), 0., center.getZ()), new SingleBlockPattern(new BaseBlock(0)) , radius, 320, false);
        editSession.makeCylinder(new Vector(center.getX()+1, 0, center.getZ()), new SingleBlockPattern(new BaseBlock(0)) , radius, 320, false);
        editSession.makeCylinder(new Vector(center.getX()-1, 0, center.getZ()), new SingleBlockPattern(new BaseBlock(0)) , radius, 320, false);
        editSession.makeCylinder(new Vector(center.getX(), 0, center.getZ()+1), new SingleBlockPattern(new BaseBlock(0)) , radius, 320, false);
        editSession.makeCylinder(new Vector(center.getX(), 0, center.getZ()-1), new SingleBlockPattern(new BaseBlock(0)) , radius, 320, false);
        editSession.flushQueue();
        editSession.commit();

    }

    public void substractRadius(double toSub) {
        this.radius-=toSub;
    }

    public double getRadius() {
        return radius;
    }

    public double getMaxRadius() {
        return maxRadius;
    }
}
