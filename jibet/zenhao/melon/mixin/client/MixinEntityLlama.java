package jibet.zenhao.melon.mixin.client;

import net.minecraft.entity.passive.EntityLlama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityLlama.class})
public class MixinEntityLlama {
   public MixinEntityLlama() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"canBeSteered"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void canBeSteered(CallbackInfoReturnable<Boolean> param1) {
      // $FF: Couldn't be decompiled
   }
}
