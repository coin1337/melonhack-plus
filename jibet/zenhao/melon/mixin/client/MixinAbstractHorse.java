package jibet.zenhao.melon.mixin.client;

import net.minecraft.entity.passive.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractHorse.class})
public class MixinAbstractHorse {
   public MixinAbstractHorse() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"isHorseSaddled"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void isHorseSaddled(CallbackInfoReturnable<Boolean> param1) {
      // $FF: Couldn't be decompiled
   }
}
