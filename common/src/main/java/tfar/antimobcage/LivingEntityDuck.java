package tfar.antimobcage;

import net.minecraft.world.entity.LivingEntity;

public interface LivingEntityDuck {

    int getBreakBlocksTick();
    void setBreakBlocksTick(int breakBlocksTick);
    static LivingEntityDuck of(LivingEntity living) {
        return (LivingEntityDuck) living;
    }

}
