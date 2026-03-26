package net.examplemod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import com.simibubi.create.content.trains.entity.CarriageEntity;

public class RedstoneTrackBlock extends Block {
    // Il livello di potenza/luce (0 = spento, 1-15 = acceso)
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 15);

    public RedstoneTrackBlock(Properties properties) {
        super(properties.lightLevel(state -> state.getValue(LEVEL)));
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && entity instanceof CarriageEntity carriage) {
            // Se un treno tocca il binario, lui diventa la "Sorgente" (Livello 15)
            updateSignal(level, pos, 15);
        }
    }

    private void updateSignal(Level level, BlockPos pos, int power) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof RedstoneTrackBlock)) return;

        // Se il nuovo potere è più alto di quello attuale, lo aggiorniamo
        if (state.getValue(LEVEL) < power) {
            level.setBlock(pos, state.setValue(LEVEL, power), 3);

            // Propagazione: se abbiamo ancora carica (potere > 8, per fare 7 blocchi)
            // Sottraiamo 2 ogni passo per limitare la gittata a 7 blocchi (15 -> 13 -> 11...)
            if (power > 1) {
                for (Direction dir : Direction.values()) {
                    if (dir.getAxis().isHorizontal()) { // Solo lungo i binari
                        updateSignal(level, pos.relative(dir), power - 2);
                    }
                }
            }
            // Programmiamo lo spegnimento
            level.scheduleTick(pos, this, 20);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Dissipazione del segnale quando il treno è passato
        if (state.getValue(LEVEL) > 0) {
            level.setBlock(pos, state.setValue(LEVEL, 0), 3);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }
}
