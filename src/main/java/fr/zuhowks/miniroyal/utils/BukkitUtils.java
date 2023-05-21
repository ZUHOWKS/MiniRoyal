package fr.zuhowks.miniroyal.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BukkitUtils {
    public static Vector getVector(Location location) {
        return new Vector(location.getX(), location.getY(), location.getZ());
    }

    public static EditSession getEditSessionFromWorldEdit(World world) {
        WorldEditPlugin WEP = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        return WEP.getWorldEdit().getEditSessionFactory().getEditSession(world, -1);
    }
}
