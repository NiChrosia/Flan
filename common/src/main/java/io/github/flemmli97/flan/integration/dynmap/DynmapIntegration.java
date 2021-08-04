package io.github.flemmli97.flan.integration.dynmap;

import io.github.flemmli97.flan.claim.Claim;
import me.shedaniel.architectury.annotations.ExpectPlatform;

public class DynmapIntegration {

    @ExpectPlatform
    static void addClaimMarker(Claim claim) {
        throw new AssertionError();
    }

    @ExpectPlatform
    static void removeMarker(Claim claim) {
        throw new AssertionError();
    }

    @ExpectPlatform
    static void changeClaimName(Claim claim) {
        throw new AssertionError();
    }

    @ExpectPlatform
    static void changeClaimOwner(Claim claim) {
        throw new AssertionError();
    }
}
