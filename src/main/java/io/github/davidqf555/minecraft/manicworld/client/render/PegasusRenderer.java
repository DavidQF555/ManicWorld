package io.github.davidqf555.minecraft.manicworld.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.manicworld.ManicWorld;
import io.github.davidqf555.minecraft.manicworld.client.model.PegasusWingsModel;
import io.github.davidqf555.minecraft.manicworld.entities.PegasusEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class PegasusRenderer extends AbstractHorseRenderer<PegasusEntity, HorseModel<PegasusEntity>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/horse/horse_white.png");

    public PegasusRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new HorseModel<>(0.1f), 1);
        addLayer(new WingsRenderer(this));
    }

    @Nonnull
    @Override
    public ResourceLocation getEntityTexture(@Nullable PegasusEntity entity) {
        return TEXTURE;
    }

    @Override
    public int getBlockLight(@Nullable PegasusEntity entityIn, @Nullable BlockPos partialTicks) {
        return 15;
    }

    @OnlyIn(Dist.CLIENT)
    private static class WingsRenderer extends LayerRenderer<PegasusEntity, HorseModel<PegasusEntity>> {

        private static final ResourceLocation TEXTURE = new ResourceLocation(ManicWorld.MOD_ID, "textures/entity/pegasus_wings.png");
        private static final PegasusWingsModel MODEL = new PegasusWingsModel();
        private static final RenderType RENDER_TYPE = RenderType.getEntityCutout(TEXTURE);

        private WingsRenderer(IEntityRenderer<PegasusEntity, HorseModel<PegasusEntity>> renderManager) {
            super(renderManager);
        }

        @Override
        public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, @Nullable PegasusEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            float scale = entitylivingbaseIn.getRenderScale();
            if (entitylivingbaseIn.isChild())
                matrixStackIn.translate(0, scale * 1.25, 0);
            matrixStackIn.scale(scale, scale, scale);
            MODEL.render(matrixStackIn, bufferIn.getBuffer(RENDER_TYPE), packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        }

        @Nonnull
        @Override
        public ResourceLocation getEntityTexture(@Nullable PegasusEntity entityIn) {
            return TEXTURE;
        }
    }

}
