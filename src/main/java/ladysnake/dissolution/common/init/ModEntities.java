package ladysnake.dissolution.common.init;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import ladysnake.dissolution.client.renders.entities.*;
import ladysnake.dissolution.common.Reference;
import ladysnake.dissolution.common.Tartaros;
import ladysnake.dissolution.common.entity.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderSpectralArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModEntities {
	
    public static void register() {
    	int id = 0;
    	EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":wandering_soul"), EntityWanderingSoul.class, "wandering_soul", id++, Tartaros.instance, 64, 1, true, 0xA8E4E4, 0x00D2D2);
    	EntityRegistry.addSpawn(EntityWanderingSoul.class, 50, 1, 1, EnumCreatureType.CREATURE, Biomes.HELL);
    	LootTableList.register(EntityWanderingSoul.LOOT);
    	EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":minion_zombie"), EntityMinionZombie.class, "minion_zombie", id++, Tartaros.instance, 64, 1, true);
    	EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":minion_pig_zombie"), EntityMinionPigZombie.class, "minion_pig_zombie", id++, Tartaros.instance, 64, 1, true);
    	EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":minion_skeleton"), EntityMinionSkeleton.class, "minion_skeleton", id++, Tartaros.instance, 64, 1, true);
    	EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":minion_stray"), EntityMinionStray.class, "minion_stray", id++, Tartaros.instance, 64, 1, true);
    	EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":minion_wither_skeleton"), EntityMinionWitherSkeleton.class, "minion_wither_skeleton", id++, Tartaros.instance, 64, 1, true);

    }
    
    @SideOnly(Side.CLIENT)
    public static void registerRenders() {
    	RenderingRegistry.registerEntityRenderingHandler(EntityWanderingSoul.class, new RenderWanderingSoul.Factory());
    	RenderingRegistry.registerEntityRenderingHandler(EntityMinionZombie.class, new RenderMinionZombie.Factory());
    	RenderingRegistry.registerEntityRenderingHandler(EntityMinionPigZombie.class, new RenderMinionPigZombie.Factory());
    	RenderingRegistry.registerEntityRenderingHandler(EntityMinionSkeleton.class, new RenderMinionSkeleton.Factory());
    	RenderingRegistry.registerEntityRenderingHandler(EntityMinionStray.class, new RenderMinionStray.Factory());
    	RenderingRegistry.registerEntityRenderingHandler(EntityMinionWitherSkeleton.class, new RenderMinionWitherSkeleton.Factory());
    	
    }

}
