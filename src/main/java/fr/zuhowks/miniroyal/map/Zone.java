package fr.zuhowks.miniroyal.map;

import fr.zuhowks.miniroyal.MiniRoyal;
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
