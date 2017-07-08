package com.amagh.bakemate.glide;

import com.bumptech.glide.load.Key;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * Created by hnoct on 6/30/2017.
 */

public class RecipeGlideSignature implements Key {
    private int currentVersion;

    public RecipeGlideSignature(int currentVersion) {
        this.currentVersion = currentVersion;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RecipeGlideSignature) {
            RecipeGlideSignature signature = (RecipeGlideSignature) obj;
            return currentVersion == signature.currentVersion;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return currentVersion;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ByteBuffer.allocate(Integer.SIZE).putInt(currentVersion).array());
    }
}
