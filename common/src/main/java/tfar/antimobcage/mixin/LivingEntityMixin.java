package tfar.antimobcage.mixin;

import net.minecraft.world.entity.LivingEntity;
import tfar.antimobcage.AntiMobCage;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.antimobcage.LivingEntityDuck;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements LivingEntityDuck {
    private int breakBlocksTick;

    @Override
    public int getBreakBlocksTick() {
        return breakBlocksTick;
    }

    @Override
    public void setBreakBlocksTick(int breakBlocksTick) {
        this.breakBlocksTick = breakBlocksTick;
    }
}