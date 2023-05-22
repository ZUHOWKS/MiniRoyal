package fr.zuhowks.miniroyal.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class BukkitUtils {
    public static Vector getVector(Location location1, Location location2) {
        return new Vector(location2.getX()-location1.getX(), location2.getY()-location1.getY(), location2.getZ()-location1.getZ());
    }

    public static double getAngleInPlanXZ(Vector vector1, Vector vector2) {
        return Math.acos((vector1.getX()*vector2.getX() + vector1.getZ()*vector2.getZ())/(Math.sqrt(Math.pow(vector1.getX(), 2) + Math.pow(vector1.getZ(), 2)) * Math.sqrt(Math.pow(vector2.getX(), 2) + Math.pow(vector2.getZ(), 2))));
    }
    public static double getVectorNormInPlanXZ(Location location1, Location location2) {
        return Math.sqrt(Math.pow(location1.getX()-location2.getX(), 2) + Math.pow(location1.getZ()-location2.getZ(), 2));
    }
}
