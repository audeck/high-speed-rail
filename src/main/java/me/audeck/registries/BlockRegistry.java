package me.audeck.registries;

import me.audeck.Main;
import me.audeck.blocks.HighSpeedRailBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class BlockRegistry {
    public static void initialize() {}

    public static final Block HIGH_SPEED_RAIL = register("high_speed_rail", new HighSpeedRailBlock(BlockBehaviour.Properties.of().noCollission().strength(0.7F).sound(SoundType.METAL)));

    private static <T extends Block> T register(String path, T block) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, path);

        Registry.register(BuiltInRegistries.BLOCK, resourceLocation, block);
        Registry.register(BuiltInRegistries.ITEM, resourceLocation, new BlockItem(block, new Item.Properties()));

        return block;
    }
}
