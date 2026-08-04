package com.charles445.simpledifficulty.block;

import net.minecraft.state.Property;

/**
 * Interface to specify which blockstate properties should be ignored for culling/rendering logic.
 * In 1.16.5, this is largely handled by the Block class directly or via custom renderers,
 * but kept for API compatibility.
 */
public interface IBlockStateIgnore {
    Property<?>[] getIgnoredProperties();
}