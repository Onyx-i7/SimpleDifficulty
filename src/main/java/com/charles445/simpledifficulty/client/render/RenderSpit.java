package com.charles445.simpledifficulty.client.render;

import com.charles445.simpledifficulty.tileentity.TileEntitySpit;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

/**
 * Renderer for the Spit tile entity, displaying cooking items.
 */
public class RenderSpit extends TileEntityRenderer<TileEntitySpit> {

    private final ItemRenderer itemRenderer;

    private final double itemBoundStart = 0.3;
    private final double itemBoundSize = 1.4142 - itemBoundStart - itemBoundStart;

    public RenderSpit(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(TileEntitySpit te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        if (te == null || te.items == null) {
            return;
        }

        matrixStack.pushPose();

        matrixStack.translate(0.5D, 0.0D, 0.5D);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(45.0F));
        matrixStack.translate(-0.5D, 0.0D, -0.5D);

        int slots = te.items.getSlots();

        double separationAmt = 0.0D;
        if (slots > 1) {
            separationAmt = itemBoundSize / (double) (slots - 1);
        }

        for (int i = 0; i < slots; i++) {
            ItemStack stack = te.items.getStackInSlot(i);
            if (!stack.isEmpty()) {
                matrixStack.pushPose();

                matrixStack.translate(0.5D, 0.25D, (i * separationAmt) + itemBoundStart);

                itemRenderer.renderStatic(stack, ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);

                matrixStack.popPose();
            }
        }

        matrixStack.popPose();
    }
}