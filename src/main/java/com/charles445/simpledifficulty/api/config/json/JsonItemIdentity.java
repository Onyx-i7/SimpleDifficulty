package com.charles445.simpledifficulty.api.config.json;

import com.charles445.simpledifficulty.SimpleDifficulty;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

/**
 * Represents an item identity used for matching items based on metadata and optional NBT data.
 * <p>
 * The identity can be a wildcard for metadata (metadata = -1 or 32767) and can optionally include
 * a partial NBT compound for matching specific item NBT tags.
 * </p>
 */
public class JsonItemIdentity
{
	// NOTE: This does not include the registry name.
	// Any comparisons made with this class assume that the registry names are already matching.

	/** The metadata value to match against. Use -1 or 32767 for wildcard. */
	public int metadata;

	/** Optional NBT tag string to match. Can be null for no NBT matching. */
	@Nullable
	public String nbt;

	/** Cached parsed NBT compound from the nbt string. Not serialized. */
	@Nullable
	private NBTTagCompound nbtCompound;

	/**
	 * Creates a new identity with the given metadata and no NBT matching.
	 *
	 * @param metadata the metadata value to match
	 */
	public JsonItemIdentity(int metadata)
	{
		this(metadata, null);
	}

	/**
	 * Creates a new identity with the given metadata and optional NBT string.
	 *
	 * @param metadata the metadata value to match
	 * @param nbt the NBT tag string to match, or null for no NBT matching
	 */
	public JsonItemIdentity(int metadata, String nbt)
	{
		this.metadata = metadata;
		this.nbt = nbt;

		tryPopulateCompound();
	}

	/**
	 * Attempts to parse the stored NBT string into an {@link NBTTagCompound}.
	 * If parsing fails, both the NBT string and the cached compound are set to null,
	 * effectively disabling NBT matching for this identity.
	 */
	public void tryPopulateCompound()
	{
		if(this.nbtCompound == null)
		{
			if(this.nbt == null)
			{
				this.nbtCompound = null;
			}
			else
			{
				try
				{
					this.nbtCompound = JsonToNBT.getTagFromJson(nbt);
					if(this.nbtCompound == null)
						throw new Exception();
				}
				catch (Exception e)
				{
					// Remove the NBT from the identity to avoid further parsing attempts
					this.nbtCompound = null;
					this.nbt = null;
					SimpleDifficulty.logger.warn("Failed to parse NBT string for JsonItemIdentity: {}", this.nbt);
				}
			}
		}
	}

	/**
	 * Checks if this identity matches the given {@link ItemStack}.
	 * The comparison includes metadata and, if this identity has NBT data, the stack's NBT compound.
	 *
	 * @param stack the ItemStack to check
	 * @return true if the stack matches this identity, false otherwise
	 */
	public boolean matches(ItemStack stack)
	{
		if(stack.hasTagCompound())
		{
			return matches(stack.getMetadata(), stack.getTagCompound());
		}
		else
		{
			return matches(stack.getMetadata(), null);
		}
	}

	/**
	 * Checks if this identity matches the given metadata, assuming no NBT data is present on the item.
	 *
	 * @param stackMetadata the metadata to check
	 * @return true if the metadata matches (and this identity has no NBT), false otherwise
	 */
	public boolean matches(int stackMetadata)
	{
		return matches(stackMetadata, null);
	}

	/**
	 * Checks if this identity matches another {@link JsonItemIdentity}.
	 * The comparison includes metadata and NBT compound if both identities have NBT data.
	 *
	 * @param sentIdentity the other identity to compare against
	 * @return true if the identities match, false otherwise
	 */
	public boolean matches(JsonItemIdentity sentIdentity)
	{
		return matches(sentIdentity.metadata, sentIdentity.nbtCompound);
	}

	/**
	 * Checks if this identity matches the given metadata and optional NBT compound.
	 * The metadata matches if this identity's metadata is -1, 32767, or equal to the given metadata.
	 * If this identity has no NBT requirement, NBT is ignored. Otherwise, the stored NBT compound
	 * must be a subset of the given stack compound.
	 *
	 * @param stackMetadata the metadata to check
	 * @param stackCompound the NBT compound of the item stack, or null if the item has no NBT
	 * @return true if the metadata and NBT (if required) match, false otherwise
	 */
	public boolean matches(int stackMetadata, @Nullable NBTTagCompound stackCompound)
	{
		// Check metadata
		if(metadata == -1 || metadata == 32767 || metadata == stackMetadata)
		{
			if(nbt == null || nbt.isEmpty())
			{
				return true;
			}
			else
			{
				// Populate the internal compound if it's null
				tryPopulateCompound();

				// Return the result of the nested compound checker
				return checkNestedCompound(this.nbtCompound, stackCompound);
			}
		}
		else
		{
			return false;
		}
	}

	/**
	 * Recursively checks whether the given stack compound contains all tags defined in the self compound.
	 * The self compound represents the NBT tags that must be present in the stack compound.
	 * <p>
	 * This method may be called during item matching when an identity includes NBT data.
	 * It is designed to be lightweight and should not cause performance issues under normal use,
	 * but avoid invoking it excessively in performance-critical paths.
	 * </p>
	 *
	 * @param selfCompound the NBT compound containing required tags (from this identity)
	 * @param stackCompound the NBT compound from the item stack to check
	 * @return true if all required tags match, false otherwise
	 */
	private boolean checkNestedCompound(NBTTagCompound selfCompound, NBTTagCompound stackCompound)
	{
		// Check if the stack has the required compound
		if(stackCompound == null)
			return false;

		// Compare all tags from the identity compound
		for(String key : selfCompound.getKeySet())
		{
			NBTBase a = selfCompound.getTag(key);
			NBTBase b = stackCompound.getTag(key);

			// continue on match, return false otherwise

			if(a != null)
			{
				// Identity tag exists and is not null

				if(a instanceof NBTTagCompound && b instanceof NBTTagCompound)
				{
					// Nested compound: check recursively
					if(checkNestedCompound((NBTTagCompound)a, (NBTTagCompound)b))
					{
						continue;
					}
					else
					{
						return false;
					}
				}
				else if(a.equals(b))
				{
					continue;
				}
				else
				{
					return false;
				}
			}
			else
			{
				// Identity tag exists but is null (unlikely)
				if(b == null)
				{
					// Matching null
					continue;
				}
				else
				{
					// Stack compound's tag of the same key is not null
					return false;
				}
			}
		}

		// All required tags matched
		return true;
	}
}