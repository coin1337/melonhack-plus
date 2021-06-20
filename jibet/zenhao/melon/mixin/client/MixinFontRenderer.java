package jibet.zenhao.melon.mixin.client;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({FontRenderer.class})
public class MixinFontRenderer {
   public MixinFontRenderer() {
      // $FF: Couldn't be decompiled
   }

   @Redirect(
      method = {"drawStringWithShadow"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;FFIZ)I"
)
   )
   public int drawCustomFontStringWithShadow(FontRenderer param1, String param2, float param3, float param4, int param5, boolean param6) {
      // $FF: Couldn't be decompiled
   }
}
