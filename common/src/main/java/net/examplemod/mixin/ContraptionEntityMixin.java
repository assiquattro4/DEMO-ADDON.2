package net.examplemod.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.examplemod.contraption.PackedContraption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractContraptionEntity.class, priority = 1000)
public abstract class ContraptionEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void stopTickingWhenPowered(CallbackInfo ci) {
        AbstractContraptionEntity entity = (AbstractContraptionEntity) (Object) this;
        
        // Se il binario sotto la contraption è alimentato, "congeliamo" il tempo per lei
        if (PackedContraption.shouldFreezeTick(entity)) {
            // Cancella l'esecuzione del tick: la contraption rimane ferma e solidale al supporto
            ci.cancel(); 
        }
    }
}
