package net.examplemod.behavior;

import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.examplemod.block.RedstoneTrackBlock;

public class RedstoneTrackMovementBehavior implements MovementBehaviour {
    @Override
    public boolean renderAsNormalBlockEntity() {
        return true;
    }

    @Override
    public void tick(MovementContext context) {
        RailShape shape = context.state.getValue(RedstoneTrackBlock.SHAPE);
        
        // Se la forma NON è nord-sud o est-ovest (quindi è una curva)
        if (shape != RailShape.NORTH_SOUTH && shape != RailShape.EAST_WEST) {
            // Lanciamo l'errore che blocca la contraption
            context.disabled = true;
            // Questo messaggio apparirà al giocatore se prova ad avviare il treno
            throw new RuntimeException("Errore: Impossibile avviare contraption con Redstone Track in curva!");
        }
    }
}
