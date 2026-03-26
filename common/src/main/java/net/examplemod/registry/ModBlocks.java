package net.examplemod.registry;

import net.examplemod.block.RedstoneTrackBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ModBlocks {
    
    // 1. Registriamo il blocco fisico nel mondo
    // Usiamo la classe RedstoneTrackBlock che abbiamo creato nel File 1
    public static final Block REDSTONE_TRACK = Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation("examplemod", "redstone_track"),
        new RedstoneTrackBlock(BlockBehaviour.Properties.of().noOcclusion().strength(1.5f))
    );

    // 2. Registriamo l'oggetto (Item) per poterlo tenere nell'inventario
    // Senza questo, il blocco esiste ma non puoi averlo in mano
    public static final Item REDSTONE_TRACK_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        new ResourceLocation("examplemod", "redstone_track"),
        new BlockItem(REDSTONE_TRACK, new Item.Properties())
    );

    // Questo metodo serve solo a "svegliare" la classe quando il mod parte
    public static void register() {
    }
}
