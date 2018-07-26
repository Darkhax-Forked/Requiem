package ladysnake.dissolution.unused.common.blocks;

import ladysnake.dissolution.unused.common.capabilities.CapabilityGenericInventoryProvider;
import ladysnake.dissolution.unused.common.tileentities.TileEntityCrucible;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class BlockCrucible extends BlockGenericContainer {
    public BlockCrucible() {
        super(Material.ROCK);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)) {
            return true;
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityCrucible) {
            IFluidHandler fluidTank = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (fluidTank != null && playerIn.getHeldItem(hand).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                FluidUtil.interactWithFluidHandler(playerIn, hand, fluidTank);
                return true;
            }
        }
        return playerIn.getHeldItem(hand).hasCapability(CapabilityGenericInventoryProvider.CAPABILITY_GENERIC, null);
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.2, 0, 0.2, 0.8, 0.7, 0.8);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileEntityCrucible();
    }
}
