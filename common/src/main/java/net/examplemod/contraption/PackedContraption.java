package net.examplemod.contraption;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.world.level.Level;
import net.examplemod.block.RedstoneTrackBlock;
import net.minecraft.core.BlockPos;

public class PackedContraption {

    // Questo metodo controlla se la contraption deve "congelarsi"
    public static boolean shouldFreezeTick(AbstractContraptionEntity entity) {
        Level level = entity.level();
        BlockPos pos = entity.blockPosition();
        
        // Controlla il blocco esattamente sotto la contraption
        if (level.getBlockState(pos).getBlock() instanceof RedstoneTrackBlock track) {
            // Se il binario è alimentato (level > 0), blocchiamo i tick
            return level.getBlockState(pos).getValue(RedstoneTrackBlock.LEVEL) > 0;
        }
        return false;
    }
}
