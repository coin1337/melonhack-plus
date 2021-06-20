package jibet.zenhao.melon.mixin.client;

import net.minecraft.client.model.ModelBoat;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ModelBoat.class})
public class MixinModelBoat {
   public MixinModelBoat() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   public void render(Entity param1, float param2, float param3, float param4, float param5, float param6, float param7, CallbackInfo param8) {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   public void render2(Entity param1, float param2, float param3, float param4, float param5, float param6, float param7, CallbackInfo param8) {
      // $FF: Couldn't be decompiled
   }
}
