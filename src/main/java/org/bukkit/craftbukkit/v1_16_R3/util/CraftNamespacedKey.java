package org.bukkit.craftbukkit.v1_16_R3.util;

import net.minecraft.util.ResourceLocation;
import org.bukkit.NamespacedKey;

public final class CraftNamespacedKey {

    public CraftNamespacedKey() {
    }

    public static NamespacedKey fromStringOrNull(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        ResourceLocation minecraft = ResourceLocation.tryParse(string);
        return (minecraft == null) ? null : fromMinecraft(minecraft);
    }

    public static NamespacedKey fromString(String string) {
        if (string == null) return null; // Mist - Guard against NPE in CraftNamespacedKey
        return fromMinecraft(new ResourceLocation(string));
    }

    public static NamespacedKey fromMinecraft(ResourceLocation minecraft) {
        if (minecraft == null) return null; // Mist - Guard against NPE in CraftNamespacedKey
        return new NamespacedKey(minecraft.getNamespace(), minecraft.getPath());
    }

    public static ResourceLocation toMinecraft(NamespacedKey key) {
        if (key == null) return null; // Mist - Guard against NPE in CraftNamespacedKey
        return new ResourceLocation(key.getNamespace(), key.getKey());
    }
}
