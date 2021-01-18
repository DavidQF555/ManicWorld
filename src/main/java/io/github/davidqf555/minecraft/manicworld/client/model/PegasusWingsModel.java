package io.github.davidqf555.minecraft.manicworld.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.davidqf555.minecraft.manicworld.common.entities.PegasusEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

import javax.annotation.Nonnull;

public class PegasusWingsModel extends EntityModel<PegasusEntity> {

    private final ModelRenderer wings;

    public PegasusWingsModel() {
        super();
        textureWidth = 64;
        textureHeight = 32;
        wings = new ModelRenderer(this);
        wings.setRotationPoint(-16, -14, -7);
        wings.setTextureOffset(0, 0).addBox(21, 0, 0, 32.0F, 32.0F, 0.0F, 0.0F, true);
        wings.setTextureOffset(0, 0).addBox(-21, 0, 0, 32.0F, 32.0F, 0.0F, 0.0F, false);
    }

    @Override
    public void setRotationAngles(@Nonnull PegasusEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        wings.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

    }
}
