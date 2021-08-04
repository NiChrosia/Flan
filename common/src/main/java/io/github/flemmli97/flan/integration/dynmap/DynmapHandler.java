package io.github.flemmli97.flan.integration.dynmap;

import io.github.flemmli97.flan.claim.Claim;

public class DynmapHandler {

    public static boolean dynmapLoaded;

    public static void updateDynmap(Claim claim, Type type) {
        if (!dynmapLoaded)
            return;
        switch (type) {
            case ADD:
                DynmapIntegration.addClaimMarker(claim);
                break;
            case OWNER:
                DynmapIntegration.changeClaimOwner(claim);
                break;
            case NAME:
                DynmapIntegration.changeClaimName(claim);
                break;
            case REMOVE:
                DynmapIntegration.removeMarker(claim);
                break;
        }
    }

    public enum Type {
        ADD,
        REMOVE,
        OWNER,
        NAME,
    }
}
