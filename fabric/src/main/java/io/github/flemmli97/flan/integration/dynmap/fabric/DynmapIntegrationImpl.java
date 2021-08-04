package io.github.flemmli97.flan.integration.dynmap.fabric;

import com.mojang.authlib.GameProfile;
import io.github.flemmli97.flan.claim.Claim;
import io.github.flemmli97.flan.integration.dynmap.DynmapHandler;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

public class DynmapIntegrationImpl {

    private static MarkerSet markerSet;
    private static final String markerID = "flan:claims", markerLabel = "Claims";

    public static void reg() {
        DynmapCommonAPIListener.register(new DynmapCommonAPIListener() {
            @Override
            public void apiEnabled(DynmapCommonAPI dynmapCommonAPI) {
                MarkerAPI markerAPI = dynmapCommonAPI.getMarkerAPI();
                markerSet = markerAPI.createMarkerSet(markerID, markerLabel, null, false);
                DynmapHandler.dynmapLoaded = true;
            }
        });
    }

    public static void addClaimMarker(Claim claim) {
        if (markerSet == null)
            return;
        int[] dim = claim.getDimensions();
        AreaMarker marker = markerSet.createAreaMarker(claim.getClaimID().toString(), claimLabel(claim), true, claim.getWorld().getRegistryKey().toString(), new double[]{dim[0], dim[1]}, new double[]{dim[2], dim[3]}, false);
        marker.setLineStyle(1, 0.9, 0x1f1f1f);
        marker.setFillStyle(0.5, setColor(claim.isAdminClaim()));
    }

    public static void removeMarker(Claim claim) {
        if (markerSet == null)
            return;
        markerSet.findAreaMarker(claim.getClaimID().toString()).deleteMarker();
    }

    public static void changeClaimName(Claim claim) {
        if (markerSet == null)
            return;
        markerSet.findAreaMarker(claim.getClaimID().toString())
                .setLabel(claimLabel(claim));
    }

    private static String claimLabel(Claim claim) {
        String name = claim.getClaimName();
        if (name == null || name.isEmpty()) {
            GameProfile prof = claim.getWorld().getServer().getUserCache().getByUuid(claim.getOwner());
            return (prof == null ? "UNKOWN" : prof.getName()) + "'s Claim";
        }
        return name;
    }

    public static void changeClaimOwner(Claim claim) {
        if (markerSet == null)
            return;
        if (claim.getClaimName() == null || claim.getClaimName().isEmpty())
            markerSet.findAreaMarker(claim.getClaimID().toString())
                    .setLabel(claimLabel(claim));
    }

    private static int setColor(boolean admin) {
        return admin ? 0xff0000 : 0xe0e01d;
    }
}
