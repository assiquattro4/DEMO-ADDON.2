package net.examplemod.block;

import net.minecraft.core.BlockPos;
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
    // Definiamo la proprietà "level" (luce/segnale) da 0 a 15
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 15);

    public RedstoneTrackBlock(Properties properties) {
        // La luce del blocco dipenderà dal valore di LEVEL
        super(properties.lightLevel(state -> state.getValue(LEVEL)));
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide) {
            // Controlla se il binario è alimentato da Redstone (Leva, Torcia, etc.)
            boolean isPowered = level.hasNeighborSignal(pos);

            // Se è alimentato e sopra c'è un treno di Create
            if (isPowered && entity instanceof CarriageEntity carriage) {
                // Ottiene la velocità del treno
                double speed = Math.abs(carriage.getCarriage().train.speed);
                int newPower;

                if (speed <= 0.05) {
                    newPower = 1; // Treno fermo = Luce 1
                } else {
                    // Treno in movimento = Luce da 2 a 15 (basata sulla velocità)
                    newPower = (int) Math.min(15, 2 + (speed * 26)); 
                }

                // Applica il nuovo livello di luce al blocco
                if (state.getValue(LEVEL) != newPower) {
                    level.setBlock(pos, state.setValue(LEVEL, newPower), 3);
                }
                
                // Programma un controllo tra 1 secondo per vedere se il treno c'è ancora
                level.scheduleTick(pos, this, 20);
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Se il treno se n'è andato, spegne la luce (torna a 0)
        if (state.getValue(LEVEL) > 0) {
            level.setBlock(pos, state.setValue(LEVEL, 0), 3);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // Dice a Minecraft che questo blocco possiede la proprietà "level"
        builder.add(LEVEL);
    }
}
