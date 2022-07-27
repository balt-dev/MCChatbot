package net.mcchatbot.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHudListener.class)
public class ReceiveMessageMixin {
	MinecraftClient client = MinecraftClient.getInstance();
	@Inject(at = @At("HEAD"), method = "net.minecraft.client.gui.hud.ChatHudListener.onChatMessage")
	public void onChatMessage(MessageType type, Text message, MessageSender sender, CallbackInfo ci){
		//don't have anything to put here yet
	}
}
