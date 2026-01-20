package com.catalytictweaks.catalytictweaksmod.mixin.mmr;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import es.degrassi.mmreborn.common.machine.component.FluidComponent;
import es.degrassi.mmreborn.common.util.HybridTank;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

@Mixin(FluidComponent.class)
public class FluidComponentMixin {
    @Shadow protected @Final HybridTank handler;
    
    @Overwrite
    public void removeFromInputs(FluidIngredient ingredient, int amount) {
        AtomicLong toRemove = new AtomicLong(amount);
        Arrays.stream(ingredient.getStacks())
            .forEach(targetFluid -> {
                if (toRemove.get() <= 0) return;
                FluidStack request = new FluidStack(targetFluid.getFluid(), (int) toRemove.get());
                FluidStack drained = handler.drain(request, HybridTank.FluidAction.EXECUTE);
          
                if (!drained.isEmpty()) {
                    toRemove.addAndGet(-drained.getAmount());
                }
            });
    }
}
