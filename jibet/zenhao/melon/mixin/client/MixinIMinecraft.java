package jibet.zenhao.melon.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Minecraft.class})
public interface MixinIMinecraft {
   @Accessor("session")
   void setSession(Session var1);
}
