package com.charles445.simpledifficulty.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;

/**
 * Utility class for rendering operations.
 * Provides helper methods for drawing textured rectangles with MatrixStack support.
 */
public class RenderUtil {

    /**
     * Draws a textured modal rectangle using the modern rendering system.
     * This is a static version of AbstractGui.blit with custom texture coordinates.
     *
     * @param matrixStack The matrix stack for transformations.
     * @param x The x position.
     * @param y The y position.
     * @param texX The texture x coordinate.
     * @param texY The texture y coordinate.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     */
    public static void drawTexturedModalRect(MatrixStack matrixStack, float x, float y, int texX, int texY, int width, int height) {
        AbstractGui.blit(matrixStack, (int) x, (int) y, texX, texY, width, height, 256, 256);
    }

    /**
     * Overload for integer coordinates.
     */
    public static void drawTexturedModalRect(MatrixStack matrixStack, int x, int y, int texX, int texY, int width, int height) {
        AbstractGui.blit(matrixStack, x, y, texX, texY, width, height, 256, 256);
    }
}