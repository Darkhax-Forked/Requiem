package ladysnake.dissolution.common.items;

import ladysnake.dissolution.api.corporeality.ISoulInteractable;
import ladysnake.dissolution.client.renders.ShaderHelper;
import ladysnake.dissolution.common.Reference;
import ladysnake.dissolution.common.handlers.CustomDissolutionTeleporter;
import ladysnake.dissolution.unused.common.entity.souls.EntityFleetingSoul;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemDebug extends Item implements ISoulInteractable {

    // yes this field is common to all item instances but I don't care, no one but me should be using this
    protected int debugWanted = 0;

    public ItemDebug() {
        super();
        this.setMaxStackSize(1);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
        if (playerIn.isSneaking()) {
            if (!worldIn.isRemote) {
                debugWanted = (debugWanted + 1) % 8;
                playerIn.sendStatusMessage(new TextComponentString("debug: " + debugWanted), true);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
        }
        switch (debugWanted) {
            case 0:
                if (worldIn.isRemote) {
                    ShaderHelper.disableScreenShader(new ResourceLocation(Reference.MOD_ID, "shaders/post/test.json"));
                    ShaderHelper.enableScreenShader(new ResourceLocation(Reference.MOD_ID, "shaders/post/test.json"));
                }
                break;
            case 1:
                if (!worldIn.isRemote) {
                    worldIn.getWorldInfo().setAllowCommands(true);
                }
                break;
            case 2:
                if (!playerIn.world.isRemote) {
                    CustomDissolutionTeleporter.transferPlayerToDimension((EntityPlayerMP) playerIn, playerIn.dimension == -1 ? 0 : -1);
                }
                break;
            case 3:
                if (!playerIn.world.isRemote) {
                    worldIn.loadedEntityList.stream().filter(e -> e instanceof EntityFleetingSoul).forEach(Entity::onKillCommand);
                    EntityFleetingSoul cam = new EntityFleetingSoul(playerIn.world, playerIn.posX + 2, playerIn.posY, playerIn.posZ);
                    worldIn.spawnEntity(cam);
                }
                break;
            case 4:
                if (!playerIn.world.isRemote) {
                    playerIn.sendStatusMessage(new TextComponentString("Printing fire information"), true);
                    List<Entity> fires = playerIn.world.getEntities(Entity.class, e -> e != null && e.getDistance(playerIn) < 20);
                    fires.forEach(System.out::println);
                }
                break;
            default:
                break;
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }
}
