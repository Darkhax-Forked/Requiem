/*
 * Requiem
 * Copyright (C) 2017-2021 Ladysnake
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses>.
 *
 * Linking this mod statically or dynamically with other
 * modules is making a combined work based on this mod.
 * Thus, the terms and conditions of the GNU General Public License cover the whole combination.
 *
 * In addition, as a special exception, the copyright holders of
 * this mod give you permission to combine this mod
 * with free software programs or libraries that are released under the GNU LGPL
 * and with code included in the standard release of Minecraft under All Rights Reserved (or
 * modified versions of such code, with unchanged license).
 * You may copy and distribute such a system following the terms of the GNU GPL for this mod
 * and the licenses of the other code concerned.
 *
 * Note that people who make modified versions of this mod are not obligated to grant
 * this special exception for their modified versions; it is their choice whether to do so.
 * The GNU General Public License gives permission to release a modified version without this exception;
 * this exception also makes it possible to release a modified version which carries forward this exception.
 */
package ladysnake.requiem.client.particle.wisp;

import ladysnake.requiem.Requiem;
import ladysnake.requiem.client.render.entity.model.WillOWispModel;
import ladysnake.requiem.common.particle.WispTrailParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.ReusableStream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.stream.Stream;

public class WillOWispParticle extends Particle {
    public final Identifier texture;
    private final Model model;
    private final RenderLayer layer;
    public float yaw;
    public float pitch;
    public float prevYaw;
    public float prevPitch;

    public float speedModifier;
    protected double xTarget;
    protected double yTarget;
    protected double zTarget;
    protected int targetChangeCooldown = 0;
    protected int timeInSolid = -1;

    protected WillOWispParticle(ClientWorld world, double x, double y, double z, Identifier texture) {
        super(world, x, y, z);
        this.texture = texture;
        this.model = new WillOWispModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(WillOWispModel.MODEL_LAYER));
        this.layer = RenderLayer.getEntityTranslucent(texture);
        this.gravityStrength = 0.0F;
        this.maxAge = 600 + random.nextInt(600);
        speedModifier = 0.1f + Math.max(0, random.nextFloat() - 0.1f);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d vec3d = camera.getPos();
        float f = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3d.getZ());

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(f, g, h);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(g, this.prevYaw, this.yaw) - 180));
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MathHelper.lerp(g, this.prevPitch, this.pitch)));
        matrixStack.scale(0.5F, -0.5F, 0.5F);
        matrixStack.translate(0, -1, 0);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vertexConsumer2 = immediate.getBuffer(this.layer);
        if (colorAlpha > 0) {
            this.model.render(matrixStack, vertexConsumer2, 15728880, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0f);
        }
        immediate.draw();
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        if (this.age++ >= this.maxAge) {
            for (int i = 0; i < 25; i++) {
                this.world.addParticle(new WispTrailParticleEffect(1.0f, 1.0f, 1.0f, -0.1f, -0.01f, 0.0f), this.x + random.nextGaussian() / 15, this.y + random.nextGaussian() / 15, this.z + random.nextGaussian() / 15, 0, 0.2d, 0);
                this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SOUL_SAND.getDefaultState()), this.x + random.nextGaussian() / 10, this.y + random.nextGaussian() / 10, this.z + random.nextGaussian() / 10, random.nextGaussian() / 20, random.nextGaussian() / 20, random.nextGaussian() / 20);
            }
            this.world.playSound(new BlockPos(this.x, this.y, this.z), SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.AMBIENT, 1.0f, 1.5f, true);
            this.world.playSound(new BlockPos(this.x, this.y, this.z), SoundEvents.BLOCK_SOUL_SAND_BREAK, SoundCategory.AMBIENT, 1.0f, 1.0f, true);
            this.markDead();
        }

        this.targetChangeCooldown -= (new Vec3d(x, y, z).squaredDistanceTo(prevPosX, prevPosY, prevPosZ) < 0.0125) ? 10 : 1;

        if ((this.world.getTime() % 20 == 0) && ((xTarget == 0 && yTarget == 0 && zTarget == 0) || new Vec3d(x, y, z).squaredDistanceTo(xTarget, yTarget, zTarget) < 9 || targetChangeCooldown <= 0)) {
            selectBlockTarget();
        }

        Vec3d targetVector = new Vec3d(this.xTarget - this.x, this.yTarget - this.y, this.zTarget - this.z);
        double length = targetVector.length();
        targetVector = targetVector.multiply(speedModifier / length);

        velocityX = (0.9) * velocityX + (0.1) * targetVector.x;
        velocityY = (0.9) * velocityY + (0.1) * targetVector.y;
        velocityZ = (0.9) * velocityZ + (0.1) * targetVector.z;

        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        Vec3d vec3d = new Vec3d(velocityX, velocityY, velocityZ);
        float f = (float) Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
        this.yaw = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875D);
        this.pitch = (float) (MathHelper.atan2(vec3d.y, f) * 57.2957763671875D);

        for (int i = 0; i < 10 * this.speedModifier; i++) {
            if (this.world.getBlockState(new BlockPos(this.x, this.y, this.z)).isIn(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
                this.world.addParticle(ParticleTypes.SOUL, this.x + random.nextGaussian() / 10, this.y + random.nextGaussian() / 10, this.z + random.nextGaussian() / 10, random.nextGaussian() / 20, random.nextGaussian() / 20, random.nextGaussian() / 20);
            } else {
                this.world.addParticle(new WispTrailParticleEffect(1.0f, 1.0f, 1.0f, -0.1f, -0.01f, 0.0f), this.x + random.nextGaussian() / 15, this.y + random.nextGaussian() / 15, this.z + random.nextGaussian() / 15, 0, 0.2d, 0);
            }
        }

        if (!new BlockPos(x, y, z).equals(this.getTargetPosition())) {
            this.move(velocityX, velocityY, velocityZ);
        }

        if (random.nextInt(20) == 0) {
            this.world.playSound(new BlockPos(this.x, this.y, this.z), SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.AMBIENT, 1.0f, 1.5f, true);
        }

        BlockPos pos = new BlockPos(this.x, this.y, this.z);
        if (!this.world.getBlockState(pos).isAir()) {
            if (timeInSolid > -1) {
                timeInSolid += 1;
            }
        } else {
            timeInSolid = 0;
        }

        if (timeInSolid > 25) {
            this.markDead();
        }
    }

    @Override
    public void move(double dx, double dy, double dz) {
        double d = dx;
        double e = dy;
        if (this.collidesWithWorld && !this.world.getBlockState(new BlockPos(this.x + dx, this.y + dy, this.z + dz)).isIn(BlockTags.SOUL_FIRE_BASE_BLOCKS) && (dx != 0.0D || dy != 0.0D || dz != 0.0D)) {
            Vec3d vec3d = Entity.adjustMovementForCollisions(null, new Vec3d(dx, dy, dz), this.getBoundingBox(), this.world, ShapeContext.absent(), new ReusableStream<>(Stream.empty()));
            dx = vec3d.x;
            dy = vec3d.y;
            dz = vec3d.z;
        }

        if (dx != 0.0D || dy != 0.0D || dz != 0.0D) {
            this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
            this.repositionFromBoundingBox();
        }

        this.onGround = dy != dy && e < 0.0D && !this.world.getBlockState(new BlockPos(this.x, this.y, this.z)).isIn(BlockTags.SOUL_FIRE_BASE_BLOCKS);
        if (d != dx) {
            this.velocityX = 0.0D;
        }

        if (dz != dz) {
            this.velocityZ = 0.0D;
        }
    }

    public BlockPos getTargetPosition() {
        return new BlockPos(this.xTarget, this.yTarget + 0.5, this.zTarget);
    }

    protected void selectBlockTarget() {
        // Behaviour
        this.xTarget = this.x + random.nextGaussian() * 10;
        this.yTarget = this.y + random.nextGaussian() * 10;
        this.zTarget = this.z + random.nextGaussian() * 10;

        BlockPos targetPos = new BlockPos(this.xTarget, this.yTarget, this.zTarget);
        if (this.world.getBlockState(targetPos).isFullCube(world, targetPos) && !this.world.getBlockState(targetPos).isIn(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
            targetChangeCooldown = 0;
            return;
        }

        speedModifier = 0.1f + Math.max(0, random.nextFloat() - 0.1f);
        targetChangeCooldown = random.nextInt() % (int) (100 / this.speedModifier);
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
        public DefaultFactory(SpriteProvider spriteProvider) {
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new WillOWispParticle(clientWorld, d, e, f, Requiem.id("textures/entity/soul.png"));
        }
    }
}
