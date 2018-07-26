package ladysnake.dissolution.unused.common.tileentities;

import ladysnake.dissolution.unused.api.GenericStack;
import ladysnake.dissolution.unused.common.EnumPowderOres;
import ladysnake.dissolution.unused.common.capabilities.CapabilityGenericInventoryProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TileEntityMortar extends PowderContainer {

    public static final Map<Item, EnumPowderOres> itemToPowder = new HashMap<>();
    private static final int MAX_VOLUME = 1;

    private int crushTime;

    public TileEntityMortar() {
        super();
        this.itemInventory = new PowderContainer.ItemHandler(Arrays.stream(EnumPowderOres.values()).map(EnumPowderOres::getComponent).toArray(Item[]::new));
    }

    public void crush() {
        if (!itemInventory.getStackInSlot(0).isEmpty()) {
            for (int i = 0; i < 10; i++) {
                Vec3d vec3d = new Vec3d((world.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
                double d0 = (-world.rand.nextFloat()) * 0.6D - 0.3D;
                Vec3d vec3d1 = new Vec3d(getPos().getX(), getPos().getY() + d0, getPos().getZ());
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z,
                        Item.getIdFromItem(itemInventory.getStackInSlot(0).getItem()));
            }
        }
        if (!world.isRemote && ++crushTime % 30 == 0) {
            ItemStack item = itemInventory.extractItem(0, 1, false);
            GenericStack<EnumPowderOres> powderStack = Arrays.stream(EnumPowderOres.values())
                    .filter(enumPowders -> enumPowders.getComponent().equals(item.getItem()))
                    .map(GenericStack::new).findAny().orElse(GenericStack.empty());
            powderInventory.insert(powderStack);
        }
    }

    @Override
    protected boolean isFull() {
        return this.powderInventory.getTotalAmount() + this.itemInventory.getStackInSlot(0).getCount() >= MAX_VOLUME;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityGenericInventoryProvider.CAPABILITY_GENERIC || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityGenericInventoryProvider.CAPABILITY_GENERIC) {
            return CapabilityGenericInventoryProvider.CAPABILITY_GENERIC.cast(inventoryProvider);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    protected int getMaxVolume() {
        return MAX_VOLUME;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return saveToNbt(super.writeToNBT(compound));
    }

}
