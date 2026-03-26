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
    // Aggiungiamo la proprietà per la forma (curve, rettilinei, etc.)
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE;
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
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // Ritorna vero solo se il blocco sotto è solido (faccia superiore completa)
        return canSupportRigidBlock(level, pos.below());
    }

    // Questo metodo rileva quando metti una leva o dai corrente al binario
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            int powerReceived = level.getBestNeighborSignal(pos);
            if (powerReceived > 0) {
                // Se riceve energia, avvia la catena di 7 blocchi (14 / 2 = 7)
                updateSignal(level, pos, 14);
            }
        }
    }

    private void updateSignal(Level level, BlockPos pos, int power) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof RedstoneTrackBlock)) return;

        // Aggiorna solo se il nuovo segnale è più forte di quello attuale
        if (state.getValue(LEVEL) < power) {
            level.setBlock(pos, state.setValue(LEVEL, power), 3);

            // Propagazione orizzontale
            if (power > 1) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    updateSignal(level, pos.relative(dir), power - 2);
                }
            }
            // Dopo 20 tick (1 secondo), il binario proverà a spegnersi se non c'è più fonte
            level.scheduleTick(pos, this, 20);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Se non riceve più segnale dai vicini, torna a 0
        if (level.getBestNeighborSignal(pos) == 0) {
            level.setBlock(pos, state.setValue(LEVEL, 0), 3);
        }
    }
}
