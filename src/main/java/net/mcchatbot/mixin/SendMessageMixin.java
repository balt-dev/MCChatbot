package net.mcchatbot.mixin;

import net.mcchatbot.ChatbotMod;
import net.mcchatbot.StackOverflowMathParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import netscape.javascript.JSException;

@Mixin(ClientPlayerEntity.class)
public class SendMessageMixin {
	MinecraftClient client = MinecraftClient.getInstance();
	public void broadcast(String message) {this.client.inGameHud.getChatHud().addMessage(Text.of(message));}
	@Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/network/ClientPlayerEntity;sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", cancellable = true)
	public void onChatMessage(String message, Text preview, CallbackInfo ci) {
		boolean cancelMessage = false;
		if (message.startsWith(".")) {
			cancelMessage = true;
			String command = message.substring(1);
			String args = "";
			if (message.contains(" ")) {
				String[] cmdArgs = message.substring(1).split(" ", 2);
				command = cmdArgs[0];
				args = cmdArgs[1];
			}
			ChatbotMod.LOGGER.info(command);
			ChatbotMod.LOGGER.info(args);
			switch (command) {
				case "c","calc","calculate" -> {
					double ans = StackOverflowMathParser.eval(args);
					if (ans % 1 == 0) { //this is probably the worst way to do this but whateverrrr
						this.broadcast("Output: " + String.format("%.0f", ans));
					} else {
						String s = String.format("%.5f", ans);
						s = s.contains(".") ? s.replaceAll("0+$", "") : s;
						this.broadcast("Output: " + s);
					}
				} //will add on to this
				default -> cancelMessage = false;
			}
		}
		if (cancelMessage) {ci.cancel();}
	}
}
