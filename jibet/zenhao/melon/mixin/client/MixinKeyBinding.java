package jibet.zenhao.melon.mixin.client;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({KeyBinding.class})
public class MixinKeyBinding {
   @Shadow
   public boolean field_74513_e;

   public MixinKeyBinding() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"isKeyDown"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void isKeyDown(CallbackInfoReturnable<Boolean> param1) {
      // $FF: Couldn't be decompiled
   }
}
