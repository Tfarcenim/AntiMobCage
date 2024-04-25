package tfar.antimobcage;

import com.google.common.collect.Lists;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Mod(AntiMobCage.MOD_ID)
public class AntiMobCageForge {
    
    public AntiMobCageForge() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onConfigLoad);
        bus.addListener(this::onConfigReLoad);
        MinecraftForge.EVENT_BUS.addListener(this::livingTick);
        MinecraftForge.EVENT_BUS.addListener(this::hurt);
        AntiMobCage.init();
    }

    private void livingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        LivingEntityDuck livingEntityDuck = LivingEntityDuck.of(living);
        int destroyBlockTicks = livingEntityDuck.getBreakBlocksTick();
        if (destroyBlockTicks > 0) {
            destroyBlockTicks--;
            livingEntityDuck.setBreakBlocksTick(destroyBlockTicks);
            if (destroyBlockTicks == 0) {
                AntiMobCage.breakNearbyBlocks(living);
            }
        }
    }

    private void hurt(LivingDamageEvent event) {
        AntiMobCage.onHurt(event.getEntity());
    }

    private void onConfigLoad(ModConfigEvent.Loading event) {
        AntiMobCage.parse(ServerConfig.entity_type_map.get());
    }

    private void onConfigReLoad(ModConfigEvent.Reloading event) {
        AntiMobCage.parse(ServerConfig.entity_type_map.get());
    }

    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<ServerConfig, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair2.getRight();
        SERVER = specPair2.getLeft();
    }
    public static class ServerConfig {
        public static ForgeConfigSpec.ConfigValue<List<? extends String>> entity_type_map;

        public static final List<String> defaults = Lists.newArrayList(
                "minecraft:creeper|3x2x3");

        public ServerConfig(ForgeConfigSpec.Builder builder) {
            builder.push("server");

            entity_type_map = builder.
                    comment("Map of entity type to block break radius'")
                    .defineList("entity_type_map", defaults,String.class::isInstance);
            builder.pop();
        }
    }

}