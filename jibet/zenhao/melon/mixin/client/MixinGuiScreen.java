package jibet.zenhao.melon.mixin.client;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiScreen.class})
public class MixinGuiScreen {
   @Shadow
   public Minecraft field_146297_k;
   RenderItem itemRender;
   FontRenderer fontRenderer;
   @Shadow
   protected List<GuiButton> field_146292_n;
   @Shadow
   public int field_146294_l;
   @Shadow
   public int field_146295_m;

   public MixinGuiScreen() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"renderToolTip"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void renderToolTip(ItemStack param1, int param2, int param3, CallbackInfo param4) {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"Lnet/minecraft/client/gui/GuiScreen;drawWorldBackground(I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void drawWorldBackgroundWrapper(int param1, CallbackInfo param2) {
      // $FF: Couldn't be decompiled
   }

   private void drawGradientRectP(int param1, int param2, int param3, int param4, int param5, int param6) {
      // $FF: Couldn't be decompiled
   }
}
