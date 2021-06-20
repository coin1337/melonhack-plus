package jibet.zenhao.melon.mixin.client;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({NetHandlerPlayClient.class})
public class MixinNetHandlerPlayClient {
   public MixinNetHandlerPlayClient() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"handleChunkData"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/world/chunk/Chunk;read(Lnet/minecraft/network/PacketBuffer;IZ)V"
)},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void read(SPacketChunkData param1, CallbackInfo param2, Chunk param3) {
      // $FF: Couldn't be decompiled
   }
}
