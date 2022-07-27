package net.mcchatbot.mixin;

import net.mcchatbot.ChatbotMod;
import net.mcchatbot.StackOverflowMathParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class SendMessageMixin {
	MinecraftClient client = MinecraftClient.getInstance();
	public void broadcast(String message) {this.client.inGameHud.getChatHud().addMessage(Text.of(message));}
	public void broadcastError(String message) {
		this.client.inGameHud.getChatHud().addMessage(Text.of(message).getWithStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000))).get(0));
	}
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
					try {
						double ans = StackOverflowMathParser.eval(args);
						if (ans % 1 == 0) { //this is probably the worst way to do this but whateverrrr
							this.broadcast("Output: " + String.format("%.0f", ans));
						} else {
							String s = String.format("%.12f", ans);
							s = s.contains(".") ? s.replaceAll("0+$", "0") : s;
							this.broadcast("Output: " + s);
						}
					} catch (RuntimeException e) {
						this.broadcastError("Expression \"" + args + "\" is not valid!");
					}
				} //will add on to this
				case "nbt" -> {
					assert this.client.player != null; //intellij yelled at me until i added this
					ItemStack heldItem = this.client.player.getItemsHand().iterator().next();
					NbtCompound nbtOrNull = heldItem.getOrCreateNbt();
					String nbtStringOrNullString = heldItem + nbtOrNull.asString();
					this.broadcast(nbtStringOrNullString.split(" ",2)[1]);
				}
				default -> cancelMessage = false;
			}
		}
		if (cancelMessage) {ci.cancel();}
	}
}
