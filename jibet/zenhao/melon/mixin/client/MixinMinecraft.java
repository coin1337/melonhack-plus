package jibet.zenhao.melon.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public class MixinMinecraft {
   @Shadow
   WorldClient field_71441_e;
   @Shadow
   EntityPlayerSP field_71439_g;
   @Shadow
   GuiScreen field_71462_r;
   @Shadow
   GameSettings field_71474_y;
   @Shadow
   GuiIngame field_71456_v;
   @Shadow
   boolean field_71454_w;
   @Shadow
   SoundHandler field_147127_av;

   public MixinMinecraft() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"displayGuiScreen"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void displayGuiScreen(GuiScreen param1, CallbackInfo param2) {
      // $FF: Couldn't be decompiled
   }

   @Redirect(
      method = {"run"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"
)
   )
   public void displayCrashReport(Minecraft param1, CrashReport param2) {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"shutdown"},
      at = {@At("HEAD")}
   )
   public void shutdown(CallbackInfo param1) {
      // $FF: Couldn't be decompiled
   }

   private void save() {
      // $FF: Couldn't be decompiled
   }

   @Redirect(
      method = {"sendClickBlockToController"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"
)
   )
   private boolean isHandActiveWrapper(EntityPlayerSP param1) {
      // $FF: Couldn't be decompiled
   }

   @Redirect(
      method = {"rightClickMouse"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;getIsHittingBlock()Z",
   ordinal = 0
),
      require = 1
   )
   private boolean isHittingBlockHook(PlayerControllerMP param1) {
      // $FF: Couldn't be decompiled
   }
}
