package tfar.antimobcage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntiMobCage {

    public static final String MOD_ID = "antimobcage";
    public static final String MOD_NAME = "AntiMobCage";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static void init() {
    }

    public static final Map<EntityType<?>, Vec3i> boundingBoxes = new HashMap<>();

    public static void parse(List<? extends String> list) {
        boundingBoxes.clear();
        for (String s : list) {
            String[] strings = s.split("\\|");
            EntityType<?> entityType = Registry.ENTITY_TYPE.get(new ResourceLocation(strings[0]));
            String[] vecStrings = strings[1].split("x");
            Vec3i vec3i = new Vec3i(Integer.parseInt(vecStrings[0]),Integer.parseInt(vecStrings[1]),Integer.parseInt(vecStrings[2]));
            boundingBoxes.put(entityType,vec3i);
        }
    }

    public static void breakNearbyBlocks(LivingEntity entity) {
        EntityType<?> type = entity.getType();
        Vec3i vec3i = boundingBoxes.get(type);
        if (vec3i != null) {
            BlockPos start = entity.blockPosition().offset(-vec3i.getX() / 2,0,-vec3i.getZ() / 2);
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            boolean flag = false;
            for (int y = 0; y < vec3i.getY();y++) {
                for (int z = 0; z < vec3i.getZ();z++) {
                    for (int x = 0; x < vec3i.getX();x++) {
                        mutableBlockPos.set(start.getX() + x,start.getY() + y,start.getZ() + z);
                        BlockState state = entity.level.getBlockState(mutableBlockPos);
                        if (canBreak(entity,state,mutableBlockPos)) {
                            flag = entity.level.destroyBlock(mutableBlockPos, true, entity);
                        }
                    }
                }
            }

            if (flag) {
                entity.level.levelEvent(null, LevelEvent.SOUND_WITHER_BLOCK_BREAK, entity.blockPosition(), 0);
            }
        }
    }

    public static void onHurt(LivingEntity entity) {
        if (boundingBoxes.containsKey(entity.getType())) {
            LivingEntityDuck livingEntityDuck = LivingEntityDuck.of(entity);
            livingEntityDuck.setBreakBlocksTick(20);
        }
    }

    static boolean canBreak(LivingEntity entity, BlockState state, BlockPos.MutableBlockPos mutableBlockPos) {
        return !state.isAir() && !state.is(BlockTags.WITHER_IMMUNE) && !state.is(BlockTags.DRAGON_IMMUNE);
    }

}