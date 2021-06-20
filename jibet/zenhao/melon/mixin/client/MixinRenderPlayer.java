package jibet.zenhao.melon.mixin.client;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderPlayer.class})
public class MixinRenderPlayer {
   public MixinRenderPlayer() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"renderEntityName"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void renderLivingLabel(AbstractClientPlayer param1, double param2, double param4, double param6, String param8, double param9, CallbackInfo param11) {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"renderEntityName"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void renderLivingLabel2(AbstractClientPlayer param1, double param2, double param4, double param6, String param8, double param9, CallbackInfo param11) {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"renderRightArm"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/client/model/ModelPlayer;swingProgress:F",
   opcode = 181
)},
      cancellable = true
   )
   public void renderRightArmBegin(AbstractClientPlayer param1, CallbackInfo param2) {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"renderRightArm"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void renderRightArmReturn(AbstractClientPlayer param1, CallbackInfo param2) {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"renderLeftArm"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/client/model/ModelPlayer;swingProgress:F",
   opcode = 181
)},
      cancellable = true
   )
   public void renderLeftArmBegin(AbstractClientPlayer param1, CallbackInfo param2) {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"renderLeftArm"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void renderLeftArmReturn(AbstractClientPlayer param1, CallbackInfo param2) {
      // $FF: Couldn't be decompiled
   }
}
