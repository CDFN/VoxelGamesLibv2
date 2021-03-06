package com.voxelgameslib.voxelgameslib.components.placeholders;

import javax.annotation.Nonnull;

import com.voxelgameslib.voxelgameslib.components.user.User;

import org.bukkit.block.Block;

/**
 * Represents a sign a user can press to cause a action
 */
public interface SignButton {

    /**
     * Executes the action associated with this button
     *
     * @param user  the user that pressed this sign
     * @param block the block of this sign
     */
    void execute(@Nonnull User user, @Nonnull Block block);
}
