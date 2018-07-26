package ladysnake.dissolution.common.inventory;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Provides various methods for managing inventories
 *
 * @author Pyrofab
 */
public final class DissolutionInventoryHelper {

    /**
     * Finds an item in the player inventory
     *
     * @param player The player to search
     * @param item   The item
     * @return The first ItemStack in the target inventory corresponding to the
     * item
     */
    public static ItemStack findItem(EntityPlayer player, Item item) {
        if ((player.getHeldItem(EnumHand.OFF_HAND)).getItem() == item) {
            return player.getHeldItem(EnumHand.OFF_HAND);
        } else if ((player.getHeldItem(EnumHand.MAIN_HAND)).getItem() == item) {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        } else {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (itemstack.getItem() == item) {
                    return itemstack;
                }
            }

            return ItemStack.EMPTY;
        }
    }

    /**
     * @param inv   the inventory to scan
     * @param stack the stack to search for
     * @return the slot from the inventory that contains a stack equivalent to the one provided
     */
    public static int getSlotFor(InventoryPlayer inv, ItemStack stack) {
        for (int i = 0; i < inv.mainInventory.size(); ++i) {
            if (!inv.mainInventory.get(i).isEmpty() && ItemStack.areItemStacksEqual(stack, inv.mainInventory.get(i))) {
                return i;
            }
        }

        return -1;
    }

    public static ItemStack findItemInstance(EntityPlayer player, Item item) {
        return findItemInstance(player, item.getClass());
    }

    public static ItemStack findItemInstance(EntityPlayer player, Class<? extends Item> item) {
        if (item.isInstance(player.getHeldItem(EnumHand.OFF_HAND).getItem())) {
            return player.getHeldItem(EnumHand.OFF_HAND);
        } else if (item.isInstance(player.getHeldItem(EnumHand.MAIN_HAND).getItem())) {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        } else {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (item.isInstance(itemstack.getItem())) {
                    return itemstack;
                }
            }

            return ItemStack.EMPTY;
        }
    }

    public static boolean compareItemStacks(ItemStack stack1, ItemStack stack2) {
        return stack2.getItem() == stack1.getItem()
                && (stack2.getMetadata() == OreDictionary.WILDCARD_VALUE || stack2.getMetadata() == stack1.getMetadata());
    }

    public static void transferEquipment(EntityLivingBase source, EntityLivingBase dest) {
        for (ItemStack stuff : source.getEquipmentAndArmor()) {
            if (stuff.isEmpty()) {
                continue;
            }
            EntityEquipmentSlot slot = EntityLiving.getSlotForItemStack(stuff);
            if (!dest.getItemStackFromSlot(slot).isEmpty()) {
                dest.entityDropItem(stuff, 0.5f);
            } else {
                dest.setItemStackToSlot(slot, stuff);
            }
            source.setItemStackToSlot(slot, ItemStack.EMPTY);
        }
    }

    private DissolutionInventoryHelper() {
    }

}
