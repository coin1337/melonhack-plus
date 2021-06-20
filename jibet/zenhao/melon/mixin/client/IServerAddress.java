package jibet.zenhao.melon.mixin.client;

import net.minecraft.client.multiplayer.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ServerAddress.class})
public interface IServerAddress {
   @Invoker("getServerAddress")
   static String[] getServerAddress(String var0) {
      throw new IllegalStateException("Mixin didnt transform this");
   }
}
