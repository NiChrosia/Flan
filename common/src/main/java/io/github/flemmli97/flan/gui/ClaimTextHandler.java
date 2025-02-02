package io.github.flemmli97.flan.gui;

import io.github.flemmli97.flan.api.permission.ClaimPermission;
import io.github.flemmli97.flan.api.permission.PermissionRegistry;
import io.github.flemmli97.flan.claim.Claim;
import io.github.flemmli97.flan.claim.PermHelper;
import io.github.flemmli97.flan.config.ConfigHandler;
import io.github.flemmli97.flan.gui.inv.SeparateInv;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ClaimTextHandler extends ServerOnlyScreenHandler<Claim> {

    private final Claim claim;

    private ClaimTextHandler(int syncId, Inventory playerInventory, Claim claim) {
        super(syncId, playerInventory, 1, claim);
        this.claim = claim;
    }

    public static void openClaimMenu(ServerPlayer player, Claim claim) {
        MenuProvider fac = new MenuProvider() {
            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                return new ClaimTextHandler(syncId, inv, claim);
            }

            @Override
            public Component getDisplayName() {
                return PermHelper.simpleColoredText(claim.parentClaim() != null ? ConfigHandler.lang.screenTitleEditorSub : ConfigHandler.lang.screenTitleEditor);
            }
        };
        player.openMenu(fac);
    }

    @Override
    protected void fillInventoryWith(Player player, SeparateInv inv, Claim claim) {
        for (int i = 0; i < 9; i++) {
            switch (i) {
                case 0:
                    ItemStack close = new ItemStack(Items.TNT);
                    close.setHoverName(ServerScreenHelper.coloredGuiText(ConfigHandler.lang.screenBack, ChatFormatting.DARK_RED));
                    inv.updateStack(i, close);
                    break;
                case 2:
                    ItemStack stack = new ItemStack(Items.OAK_SIGN);
                    stack.setHoverName(ServerScreenHelper.coloredGuiText(ConfigHandler.lang.screenEnterText, ChatFormatting.GOLD));
                    if (claim.enterTitle != null)
                        ServerScreenHelper.addLore(stack, claim.enterTitle);
                    inv.updateStack(i, stack);
                    break;
                case 3:
                    ItemStack stack2 = new ItemStack(Items.OAK_SIGN);
                    stack2.setHoverName(ServerScreenHelper.coloredGuiText(ConfigHandler.lang.screenEnterSubText, ChatFormatting.GOLD));
                    if (claim.enterSubtitle != null)
                        ServerScreenHelper.addLore(stack2, claim.enterSubtitle);
                    inv.updateStack(i, stack2);
                    break;
                case 4:
                    ItemStack stack3 = new ItemStack(Items.OAK_SIGN);
                    stack3.setHoverName(ServerScreenHelper.coloredGuiText(ConfigHandler.lang.screenLeaveText, ChatFormatting.GOLD));
                    if (claim.leaveTitle != null)
                        ServerScreenHelper.addLore(stack3, claim.leaveTitle);
                    inv.updateStack(i, stack3);
                    break;
                case 5:
                    ItemStack stack4 = new ItemStack(Items.OAK_SIGN);
                    stack4.setHoverName(ServerScreenHelper.coloredGuiText(ConfigHandler.lang.screenLeaveSubText, ChatFormatting.GOLD));
                    if (claim.leaveSubtitle != null)
                        ServerScreenHelper.addLore(stack4, claim.leaveSubtitle);
                    inv.updateStack(i, stack4);
                    break;
                default:
                    inv.updateStack(i, ServerScreenHelper.emptyFiller());
            }
        }
    }

    @Override
    protected boolean isRightSlot(int slot) {
        return slot == 0 || slot == 2 || slot == 3 || slot == 4 || slot == 5 || slot == 8;
    }

    @Override
    protected boolean handleSlotClicked(ServerPlayer player, int index, Slot slot, int clickType) {
        if (index == 0) {
            player.closeContainer();
            player.getServer().execute(() -> ClaimMenuScreenHandler.openClaimMenu(player, this.claim));
            ServerScreenHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, 1, 1f);
        } else {
            Consumer<Component> cons = null;
            switch (index) {
                case 2:
                    cons = text -> this.claim.setEnterTitle(text, this.claim.enterSubtitle);
                    break;
                case 3:
                    cons = text -> this.claim.setEnterTitle(this.claim.enterTitle, text);
                    break;
                case 4:
                    cons = text -> this.claim.setLeaveTitle(text, this.claim.leaveSubtitle);
                    break;
                case 5:
                    cons = text -> this.claim.setLeaveTitle(this.claim.leaveTitle, text);
                    break;
            }
            if (cons != null) {
                player.closeContainer();
                Consumer<Component> finalCons = cons;
                if (clickType == 0) {
                    player.getServer().execute(() -> StringResultScreenHandler.createNewStringResult(player, (s) -> {
                        player.closeContainer();
                        finalCons.accept(new TextComponent(s).withStyle(Style.EMPTY.withItalic(false).applyFormat(ChatFormatting.WHITE)));
                        player.getServer().execute(() -> ClaimTextHandler.openClaimMenu(player, this.claim));
                        ServerScreenHelper.playSongToPlayer(player, SoundEvents.ANVIL_USE, 1, 1f);
                    }, () -> {
                        player.closeContainer();
                        player.getServer().execute(() -> ClaimTextHandler.openClaimMenu(player, this.claim));
                        ServerScreenHelper.playSongToPlayer(player, SoundEvents.VILLAGER_NO, 1, 1f);
                    }));
                } else {
                    TextComponent text = new TextComponent(ConfigHandler.lang.chatClaimTextEdit);
                    String command = "/flan claimMessage" + (index == 2 || index == 3 ? " enter" : " leave")
                            + (index == 2 || index == 4 ? " title" : " subtitle") + " text ";
                    text.withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
                    player.displayClientMessage(text, false);
                }
                ServerScreenHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, 1, 1f);
            }
        }
        return true;
    }

    private boolean hasEditPerm(Claim claim, ServerPlayer player) {
        return ((claim.parentClaim() != null && claim.parentClaim().canInteract(player, PermissionRegistry.EDITPERMS, player.blockPosition()))
                || claim.canInteract(player, PermissionRegistry.EDITPERMS, player.blockPosition()));
    }

    private boolean hasPerm(Claim claim, ServerPlayer player, ClaimPermission perm) {
        if (claim.parentClaim() != null)
            return claim.parentClaim().canInteract(player, perm, player.blockPosition());
        return claim.canInteract(player, perm, player.blockPosition());
    }
}
