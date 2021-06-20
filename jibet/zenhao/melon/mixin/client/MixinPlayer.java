package jibet.zenhao.melon.mixin.client;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityPlayer.class})
public class MixinPlayer extends MixinEntity {
   public MixinPlayer() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"travel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void travel(float param1, float param2, float param3, CallbackInfo param4) {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"getCooldownPeriod"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void getCooldownPeriod(CallbackInfoReturnable<Float> param1) {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"isEntityInsideOpaqueBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void isEntityInsideOpaqueBlockHook(CallbackInfoReturnable<Boolean> param1) {
      // $FF: Couldn't be decompiled
   }
}
