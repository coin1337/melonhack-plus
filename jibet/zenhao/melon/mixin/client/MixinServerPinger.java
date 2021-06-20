package jibet.zenhao.melon.mixin.client;

import java.net.UnknownHostException;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.ServerPinger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerPinger.class})
public class MixinServerPinger {
   public MixinServerPinger() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"ping"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void pingHook(ServerData param1, CallbackInfo param2) throws UnknownHostException {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"tryCompatibilityPing"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void tryCompatibilityPingHook(ServerData param1, CallbackInfo param2) {
      // $FF: Couldn't be decompiled
   }
}
