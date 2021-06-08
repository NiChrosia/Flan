package com.flemmli97.flan.mixin;

import com.flemmli97.flan.claim.Claim;
import com.flemmli97.flan.claim.ClaimStorage;
import com.flemmli97.flan.event.EntityInteractEvents;
import com.flemmli97.flan.player.IPlayerClaimImpl;
import com.flemmli97.flan.player.PlayerClaimData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerClaimMixin implements IPlayerClaimImpl {

    @Unique
    private PlayerClaimData claimData;

    @Unique
    private Claim currentClaim;

    @Shadow
    private MinecraftServer server;

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void initData(CallbackInfo info) {
        this.claimData = new PlayerClaimData((ServerPlayerEntity) (Object) this);
    }

    @Inject(method = "readCustomDataFromTag", at = @At("RETURN"))
    private void readData(CompoundTag tag, CallbackInfo info) {
        this.claimData.read(this.server);
    }

    @Inject(method = "writeCustomDataToTag", at = @At("RETURN"))
    private void writeData(CompoundTag tag, CallbackInfo info) {
        this.claimData.save(this.server);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tickData(CallbackInfo info) {
        this.claimData.tick();
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void copyOld(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo info) {
        this.claimData.clone(PlayerClaimData.get(oldPlayer));
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void canStay(CallbackInfo info) {
        EntityInteractEvents.updateClaim((ServerPlayerEntity)(Object)this, claim->this.currentClaim = claim);
    }

    @Override
    public PlayerClaimData get() {
        return this.claimData;
    }

    @Override
    public Claim getCurrentClaim() {
        return this.currentClaim;
    }
}
