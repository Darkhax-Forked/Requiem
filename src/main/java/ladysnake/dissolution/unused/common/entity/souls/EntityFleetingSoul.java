package ladysnake.dissolution.unused.common.entity.souls;

import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import ladysnake.dissolution.common.entity.SoulType;
import ladysnake.dissolution.common.inventory.DissolutionInventoryHelper;
import ladysnake.dissolution.common.items.ItemSoulInAJar;
import ladysnake.dissolution.unused.api.Soul;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;

@Optional.Interface(iface = "elucent.albedo.lighting.ILightProvider", modid = "albedo", striprefs = true)
public class EntityFleetingSoul extends AbstractSoul implements ILightProvider {

    protected int delayBeforeCanPickup = 10;
    protected int targetChangeCooldown = 0;
    protected Entity targetEntity;
    protected BlockPos forcedTarget = BlockPos.ORIGIN;

    public EntityFleetingSoul(World worldIn) {
        super(worldIn);
    }

    public EntityFleetingSoul(World worldIn, double x, double y, double z) {
        this(worldIn, x, y, z, Soul.UNDEFINED);
    }

    public EntityFleetingSoul(World worldIn, double x, double y, double z, Soul soulIn) {
        super(worldIn, soulIn);
        this.setPosition(x, y, z);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.posY > 300) {
            this.outOfWorld();
        }

        if (this.delayBeforeCanPickup > 0) {
            --this.delayBeforeCanPickup;
        }

        if (!this.world.isRemote && !this.isDead) {

            this.targetChangeCooldown -= (this.getPositionVector().squareDistanceTo(lastTickPosX, lastTickPosY, lastTickPosZ) < 0.0125) ? 10 : 1;

            // Change the target position regularly to simulate erratic movement
            if ((xTarget == 0 && yTarget == 0 && zTarget == 0) || this.getPosition().distanceSq(xTarget, yTarget, zTarget) < 9 || targetChangeCooldown <= 0) {
                selectBlockTarget();
            }

            // every so often, update the target entity
            if (this.soulAge % 100 == 0) {
                if (!(this.targetEntity instanceof EntityPlayer) || this.getDistanceSq(targetEntity) > 1024.0) {
                    this.targetEntity = selectTarget();
                    if (targetEntity == null && !world.isAnyPlayerWithinRangeAt(this.posX, this.posY, this.posZ, 64)) {
                        this.soulAge += 600;
                    }
                }
            }

            // don't target spectator players
            if (this.targetEntity instanceof EntityPlayer && ((EntityPlayer) this.targetEntity).isSpectator()) {
                this.targetEntity = null;
            }

            // targeted entities take precedence over target positions
            double targetX = this.targetEntity != null ? targetEntity.posX : this.xTarget;
            double targetY = this.targetEntity != null ? targetEntity.posY : this.yTarget;
            double targetZ = this.targetEntity != null ? targetEntity.posZ : this.zTarget;
            Vec3d targetVector = new Vec3d(targetX - posX, targetY - posY, targetZ - posZ);
            double length = targetVector.length();
            targetVector = targetVector.scale(0.1 / length);
            double weight = 0;
            // the range at which the wisp will start orbiting
            final double outerRange = 6;
            // the range at which the wisp will stop approaching its target
            final double innerRange = 1;
            if (length > innerRange && length < outerRange) {
                weight = 0.25 * ((outerRange - length) / (outerRange - innerRange));
            } else if (length <= innerRange) {
                weight = 1;
            }
            motionX = (1 - weight) * ((0.9) * motionX + (0.1) * targetVector.x) + weight * targetVector.z;
            motionY = (1 - weight) * ((0.9) * motionY + (0.1) * targetVector.y) + weight * targetVector.y;
            motionZ = (1 - weight) * ((0.9) * motionZ + (0.1) * targetVector.z) - weight * targetVector.x;
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        }
    }

    protected void selectBlockTarget() {
        if(this.forcedTarget == BlockPos.ORIGIN) {
            this.xTarget = this.posX + rand.nextGaussian() * 10;
            this.yTarget = Math.max(this.posY + rand.nextGaussian() * 10, (this.soulAge / 20.0));
            this.zTarget = this.posZ + rand.nextGaussian() * 10;
        } else {
            this.xTarget = forcedTarget.getX() + 0.5;
            this.yTarget = forcedTarget.getY() + rand.nextGaussian();
            this.zTarget = forcedTarget.getZ() + 0.5;
        }
        targetChangeCooldown = rand.nextInt() % 200;
    }

    protected Entity selectTarget() {
//                    this.targetEntity = this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 32.0,
//                            player -> player != null && !((EntityPlayer) player).isSpectator()
//                                    && !DissolutionInventoryHelper.findItem((EntityPlayer) player, ModItems.HALITE).isEmpty());
//                    if (targetEntity == null) {
//                        List<EntityItem> items = this.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(getPosition()).grow(16),
//                                item -> item != null && item.getItem().getItem() == ModItems.HALITE);
//                        if (items.size() > 0)
//                            this.targetEntity = items.get(0);
//                    }
        return null;
    }

    public boolean canBePickupBy(EntityLivingBase entity) {
        return !world.isRemote && this.delayBeforeCanPickup <= 0;
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn) {
        ItemStack bottle = DissolutionInventoryHelper.findItem(entityIn, Items.GLASS_BOTTLE/*ModItems.GLASS_JAR*/);
        if (!world.isRemote && !bottle.isEmpty() && this.delayBeforeCanPickup <= 0) {
            bottle.shrink(1);
            entityIn.addItemStackToInventory(ItemSoulInAJar.newTypedSoulBottle(SoulType.WILL_O_WISP));
            this.setDead();
        }
    }

    public SoulType getSoulType() {
        return SoulType.WILL_O_WISP;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    public boolean isCreatureType(@Nonnull EnumCreatureType type, boolean forSpawnCount) {
        return type == EnumCreatureType.AMBIENT;
    }

    @Override
    public Light provideLight() {
        return Light.builder().pos(this).radius(5).color(0.5f, 0.5f, 0.8f).build();
    }
}
