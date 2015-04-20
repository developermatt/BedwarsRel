package io.github.yannici.bedwars.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;

public class Region {

	private Location minCorner = null;
	private Location maxCorner = null;
	private World world = null;
	private String name = null;
	private List<Block> placedBlocks = null;
	private List<Block> breakedBlocks = null;
	private HashMap<Block, Integer> breakedBlockTypes = null;
	private HashMap<Block, Byte> breakedBlockData = null;

	public Region(Location pos1, Location pos2, String name) {
		if (pos1 == null || pos2 == null) {
			return;
		}

		if (!pos1.getWorld().getName().equals(pos2.getWorld().getName())) {
			return;
		}
		
		this.world = pos1.getWorld();
		this.setMinMax(pos1, pos2);
		this.placedBlocks = new ArrayList<Block>();
		this.breakedBlocks = new ArrayList<Block>();
		this.breakedBlockTypes = new HashMap<Block, Integer>();
		this.breakedBlockData = new HashMap<Block, Byte>();
		this.name = name;
	}

	public Region(World w, int x1, int y1, int z1, int x2, int y2, int z2) {
		this(new Location(w, x1, y1, z1), new Location(w, x2, y2, z2), w.getName());
	}

	public boolean check() {
		return (this.minCorner != null && this.maxCorner != null && this.world != null);
	}

	private void setMinMax(Location pos1, Location pos2) {
		this.minCorner = this.getMinimumCorner(pos1, pos2);
		this.maxCorner = this.getMaximumCorner(pos1, pos2);
	}

	private Location getMinimumCorner(Location pos1, Location pos2) {
		return new Location(this.world, Math.min(pos1.getBlockX(),
				pos2.getBlockX()),
				Math.min(pos1.getBlockY(), pos2.getBlockY()), Math.min(
						pos1.getBlockZ(), pos2.getBlockZ()));
	}

	private Location getMaximumCorner(Location pos1, Location pos2) {
		return new Location(this.world, Math.max(pos1.getBlockX(),
				pos2.getBlockX()),
				Math.max(pos1.getBlockY(), pos2.getBlockY()), Math.max(
						pos1.getBlockZ(), pos2.getBlockZ()));
	}

	public boolean isInRegion(Location location) {
		return (location.getBlockX() >= this.minCorner.getBlockX()
				&& location.getBlockX() <= this.maxCorner.getBlockX()
				&& location.getBlockY() >= this.minCorner.getBlockY()
				&& location.getBlockY() <= this.maxCorner.getBlockY()
				&& location.getBlockZ() >= this.minCorner.getBlockZ() && location
				.getBlockZ() <= this.maxCorner.getBlockZ());
	}

	@SuppressWarnings("deprecation")
    public void reset() {
		for(Block placed : this.placedBlocks) {
		    Block blockInWorld = this.world.getBlockAt(placed.getLocation());
		    if(blockInWorld.getType() == Material.AIR) {
		        continue;
		    }
		    
		    if(blockInWorld.equals(placed)) {
		        blockInWorld.setType(Material.AIR);
		    }
		}
		
		this.placedBlocks.clear();
		
		for(Block block : this.breakedBlocks) {
		    Block theBlock = this.getWorld().getBlockAt(block.getLocation());
		    theBlock.setTypeId(this.breakedBlockTypes.get(block));
		    theBlock.setData(this.breakedBlockData.get(block));
		}
		
		this.breakedBlocks.clear();

		Iterator<Entity> entityIterator = this.world.getEntities().iterator();
		while (entityIterator.hasNext()) {
			Entity e = entityIterator.next();
			if (e instanceof Item) {
				e.remove();
			}

			if (e instanceof LivingEntity) {
				LivingEntity le = (LivingEntity) e;
				le.setRemoveWhenFarAway(false);
			}
		}
	}

	public World getWorld() {
		return this.minCorner.getWorld();
	}

    public boolean isPlacedBlock(Block block) {
       return this.placedBlocks.contains(block);
    }

    @SuppressWarnings("deprecation")
	public void addPlacedBlock(Block placeBlock, BlockState replacedBlock) {
        this.placedBlocks.add(placeBlock);
        if(replacedBlock != null) {
        	this.breakedBlockTypes.put(replacedBlock.getBlock(), replacedBlock.getTypeId());
        	this.breakedBlockData.put(replacedBlock.getBlock(), replacedBlock.getData().getData());
        	this.breakedBlocks.add(replacedBlock.getBlock());
        }
    }
    
    public void removePlacedBlock(Block block) {
        this.placedBlocks.remove(block);
    }
    
    public String getName() {
    	if(this.name == null) {
    		this.name = this.world.getName();
    	}
    	
    	return this.name;
    }
    
    @SuppressWarnings("deprecation")
    public void addBreakedBlock(Block bedBlock) {
        this.breakedBlockTypes.put(bedBlock, bedBlock.getTypeId());
        this.breakedBlockData.put(bedBlock, bedBlock.getData());
        this.breakedBlocks.add(bedBlock);
    }

}
