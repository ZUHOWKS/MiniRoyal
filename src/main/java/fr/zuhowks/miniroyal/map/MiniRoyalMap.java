package fr.zuhowks.miniroyal.map;


import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import fr.zuhowks.miniroyal.MiniRoyal;
import fr.zuhowks.miniroyal.map.chests.ChestRegistry;
import fr.zuhowks.miniroyal.utils.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class MiniRoyalMap {

    private Location pos1;
    private Location pos2;
    private Location finishZoneCenter;
    private int finishZoneRadius;

    private final MiniRoyal INSTANCE = MiniRoyal.getINSTANCE();

    private final ChestRegistry chestRegistry;

    public MiniRoyalMap(Location pos1, Location pos2, Location finishZoneCenter, int finishZoneRadius) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.finishZoneCenter = finishZoneCenter;
        this.finishZoneRadius = finishZoneRadius;
        this.chestRegistry = new ChestRegistry();
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public ChestRegistry getChestRegistry() {
        return chestRegistry;
    }

    public Location getFinishZoneCenter() {
        return finishZoneCenter;
    }

    public int getFinishZoneRadius() {
        return finishZoneRadius;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public void setFinishZoneCenter(Location finishZoneCenter) {
        this.finishZoneCenter = finishZoneCenter;
    }

    public void setFinishZoneRadius(int finishZoneRadius) {
        this.finishZoneRadius = finishZoneRadius;
    }

    public void loadMapFromFile() {
        FileConfiguration config = INSTANCE.getFileConfiguration();
        this.setPos1((Location) config.get("map.pos1"));
        this.setPos2((Location) config.get("map.pos2"));
        this.setFinishZoneCenter((Location) config.get("map.finish-zone-center"));
        this.getChestRegistry().setChestLocations((List<Location>) config.getList("map.chests"));
        INSTANCE.updateZone();
    }

    public void saveMapToFile() {
        FileConfiguration config = INSTANCE.getFileConfiguration();
        config.set("map.pos1", this.getPos1());
        config.set("map.pos2", this.getPos2());
        config.set("map.finish-zone-center", this.getFinishZoneCenter());
        config.set("map.chests", this.getChestRegistry().getChestLocations());
        INSTANCE.saveConfig();
        INSTANCE.updateZone();
    }

    public void saveBuild() throws IOException, WorldEditException {
        World world = new BukkitWorld(this.getPos1().getWorld());
        CuboidRegion region = new CuboidRegion(BukkitUtils.getVector(getPos1()), BukkitUtils.getVector(getPos2()));
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                BukkitUtils.getEditSessionFromWorldEdit(world), region, clipboard, region.getMinimumPoint()
        );
        Operations.complete(forwardExtentCopy);
        ClipboardWriter writer = ClipboardFormat.SCHEMATIC.getWriter(Files.newOutputStream(getSchematicFile().toPath()));
        writer.write(clipboard, world.getWorldData());
    }

    public void loadBuild() throws IOException, WorldEditException {
        Clipboard clipboard;
        World world = new BukkitWorld(this.getPos1().getWorld());
        ClipboardReader reader = ClipboardFormat.SCHEMATIC.getReader(Files.newInputStream(getSchematicFile().toPath()));
        clipboard = reader.read(world.getWorldData());
        Operation operation = new ClipboardHolder(clipboard, world.getWorldData())
                .createPaste(BukkitUtils.getEditSessionFromWorldEdit(world), world.getWorldData())
                .to(new BlockVector(clipboard.getMinimumPoint()))
                .build();
        Operations.complete(operation);
    }

    public void loadBuildWithCuboidClipboard() throws IOException, DataException, MaxChangedBlocksException {
        World world = new BukkitWorld(this.getPos1().getWorld());
        CuboidClipboard clipboard = MCEditSchematicFormat.getFormat(getSchematicFile()).load(getSchematicFile());
        clipboard.paste(BukkitUtils.getEditSessionFromWorldEdit(world), Vector.getMinimum(BukkitUtils.getVector(getPos1()), BukkitUtils.getVector(getPos2())), false);
    }

    private File getSchematicFile() throws IOException {
        File file = new File(INSTANCE.getDataFolder() + File.separator + "map.schematic");
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
}
