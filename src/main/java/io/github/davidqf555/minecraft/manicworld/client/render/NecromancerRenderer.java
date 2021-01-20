package io.github.davidqf555.minecraft.manicworld.client.render;

import io.github.davidqf555.minecraft.manicworld.ManicWorld;
import io.github.davidqf555.minecraft.manicworld.common.entities.NecromancerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.EvokerRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NecromancerRenderer extends EvokerRenderer<NecromancerEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ManicWorld.MOD_ID, "textures/entity/necromancer_entity.png");

    public NecromancerRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        entityModel.func_205062_a().showModel = true;
    }

    @Nonnull
    @Override
    public ResourceLocation getEntityTexture(@Nullable NecromancerEntity entity) {
        return TEXTURE;
    }
}
