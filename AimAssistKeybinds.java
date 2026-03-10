package com.aimassist;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AimAssistKeybinds {

    private static KeyBinding toggleKey;
    private static KeyBinding increaseRangeKey;
    private static KeyBinding decreaseRangeKey;

    public static void register() {
        // Aim assist yoqish/o'chirish - R tugmasi
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aimassist.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.aimassist"
        ));

        // Diapazoni oshirish - + tugmasi
        increaseRangeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aimassist.increase_range",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_EQUAL,
            "category.aimassist"
        ));

        // Diapazoni kamaytirish - - tugmasi
        decreaseRangeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aimassist.decrease_range",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_MINUS,
            "category.aimassist"
        ));

        // Tugma bosilishini tekshirish
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (toggleKey.wasPressed()) {
                AimAssistMod.isEnabled = !AimAssistMod.isEnabled;
                String status = AimAssistMod.isEnabled ? "§aYOQILDI" : "§cO'CHIRILDI";
                client.player.sendMessage(
                    Text.literal("§6[Aim Assist] §r" + status),
                    true // Action bar'da ko'rsatish
                );
            }

            while (increaseRangeKey.wasPressed()) {
                if (AimAssistMod.aimRange < 10.0) {
                    AimAssistMod.aimRange = Math.min(10.0, AimAssistMod.aimRange + 1.0);
                    client.player.sendMessage(
                        Text.literal("§6[Aim Assist] §rDiapazon: §e" + (int)AimAssistMod.aimRange + " blok"),
                        true
                    );
                }
            }

            while (decreaseRangeKey.wasPressed()) {
                if (AimAssistMod.aimRange > 2.0) {
                    AimAssistMod.aimRange = Math.max(2.0, AimAssistMod.aimRange - 1.0);
                    client.player.sendMessage(
                        Text.literal("§6[Aim Assist] §rDiapazon: §e" + (int)AimAssistMod.aimRange + " blok"),
                        true
                    );
                }
            }
        });
    }
}
