package jibet.zenhao.melon.mixin.client;

import net.minecraft.entity.passive.EntityPig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityPig.class})
public class MixinEntityPig {
   public MixinEntityPig() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"canBeSteered"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void canBeSteered(CallbackInfoReturnable param1) {
      // $FF: Couldn't be decompiled
   }
}
