package com.charles445.simpledifficulty.api.config.json;

import com.charles445.simpledifficulty.SimpleDifficulty;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import javax.annotation.Nullable;

/**
 * JSON data class representing an item identity for configuration matching.
 * <p>
 * In 1.16.5, items no longer use metadata for variants. Identity is now determined by
 * the item's registry name and optionally its NBT compound tag.
 * </p>
 */
public class JsonItemIdentity {
    // Note: This does not store the registry name.
    // Any comparisons made with this assume that the registry names are already matching.

    @Nullable
    public String nbt;

    @Nullable
    private CompoundNBT nbtCompound;

    public JsonItemIdentity() {
        this(null);
    }

    public JsonItemIdentity(@Nullable String nbt) {
        this.nbt = nbt;
        this.tryPopulateCompound();
    }

    /**
     * Attempts to parse the NBT string into a CompoundNBT.
     */
    public void tryPopulateCompound() {
        if (this.nbtCompound == null) {
            try {
                this.nbtCompound = JsonToNBT.parseTag(this.nbt);
                if (this.nbtCompound == null) {
                    throw new CommandSyntaxException(null, "Failed to parse NBT");
                }
            } catch (CommandSyntaxException e) {
                SimpleDifficulty.LOGGER.warn("Failed to parse NBT string in JsonItemIdentity: {}", this.nbt);
                this.nbtCompound = null;
                this.nbt = null;
            }
        }
    }

    /**
     * Checks if this identity matches the given ItemStack.
     *
     * @param stack The ItemStack to check.
     * @return true if the identity matches.
     */
    public boolean matches(ItemStack stack) {
        return matches(stack.getTag());
    }

    /**
     * Checks if this identity matches another identity.
     *
     * @param sentIdentity The identity to compare against.
     * @return true if the identities match.
     */
    public boolean matches(JsonItemIdentity sentIdentity) {
        return matches(sentIdentity.nbtCompound);
    }

    /**
     * Checks if this identity matches an ItemStack with specific NBT.
     *
     * @param stackCompound The NBT compound to check against.
     * @return true if the identity matches.
     */
    public boolean matches(@Nullable CompoundNBT stackCompound) {
        if (nbt == null || nbt.isEmpty()) {
            return true;
        } else {
            tryPopulateCompound();
            return checkNestedCompound(this.nbtCompound, stackCompound);
        }
    }

    /**
     * Recursively checks if all tags in selfCompound exist and match in stackCompound.
     *
     * @param selfCompound The identity's NBT compound.
     * @param stackCompound The ItemStack's NBT compound.
     * @return true if all required tags match.
     */
    private boolean checkNestedCompound(CompoundNBT selfCompound, CompoundNBT stackCompound) {
        if (stackCompound == null) {
            return false;
        }

        for (String key : selfCompound.getAllKeys()) {
            INBT a = selfCompound.get(key);
            INBT b = stackCompound.get(key);

            if (a != null) {
                if (a instanceof CompoundNBT && b instanceof CompoundNBT) {
                    if (!checkNestedCompound((CompoundNBT) a, (CompoundNBT) b)) {
                        return false;
                    }
                } else if (!a.equals(b)) {
                    return false;
                }
            } else {
                if (b != null) {
                    return false;
                }
            }
        }

        return true;
    }
}