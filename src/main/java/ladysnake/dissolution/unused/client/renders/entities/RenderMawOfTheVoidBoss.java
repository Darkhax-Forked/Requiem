package ladysnake.dissolution.unused.client.renders.entities;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class RenderMawOfTheVoidBoss extends RenderLiving {

    public RenderMawOfTheVoidBoss(RenderManager renderManagerIn, ModelBiped modelBipedIn, float shadowSize) {
        super(renderManagerIn, modelBipedIn, shadowSize);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull Entity entity) {
        // TODO Auto-generated method stub
        return null;
    }

}
