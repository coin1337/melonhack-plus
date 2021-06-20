package jibet.zenhao.melon.mixin.client;

import net.minecraft.client.entity.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(
   value = {AbstractClientPlayer.class},
   priority = 2147483646
)
public class MixinAbstractClientPlayer extends MixinPlayer {
   public MixinAbstractClientPlayer() {
      // $FF: Couldn't be decompiled
   }
}
