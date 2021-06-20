package jibet.zenhao.melon.mixin.accessor;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;

public interface IMinecraft {
   Timer getTimer();

   void setSession(Session var1);

   Session getSession();

   void setRightClickDelayTimer(int var1);

   void clickMouse();

   ServerData getCurrentServerData();
}
