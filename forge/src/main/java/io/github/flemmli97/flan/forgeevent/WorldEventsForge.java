package io.github.flemmli97.flan.forgeevent;

import io.github.flemmli97.flan.api.PermissionRegistry;
import io.github.flemmli97.flan.claim.ClaimStorage;
import io.github.flemmli97.flan.claim.IPermissionContainer;
import io.github.flemmli97.flan.event.WorldEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.PistonEvent;

public class WorldEventsForge {

    public static void modifyExplosion(ExplosionEvent.Detonate event) {
        if (event.getWorld() instanceof ServerWorld)
            WorldEvents.modifyExplosion(event.getExplosion(), (ServerWorld) event.getWorld());
    }

    public static void pistonCanPush(PistonEvent.Pre event) {
        if (!(event.getWorld() instanceof World))
            return;
        if (!WorldEvents.pistonCanPush(event.getState(), (World) event.getWorld(), event.getPos(), event.getDirection(), event.getDirection()))
            event.setCanceled(true);
    }

    public static void preventMobSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (!(event.getWorld() instanceof ServerWorld) || !(event.getEntityLiving() instanceof MobEntity))
            return;
        if (WorldEvents.preventMobSpawn((ServerWorld) event.getWorld(), (MobEntity) event.getEntityLiving()))
            event.setCanceled(true);
    }
}
