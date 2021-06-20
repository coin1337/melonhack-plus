package org.spongepowered.asm.mixin.injection.invoke.arg;

public abstract class Args {
   protected final Object[] values;

   protected Args(Object[] var1) {
      this.values = var1;
   }

   public int size() {
      return this.values.length;
   }

   public <T> T get(int var1) {
      return this.values[var1];
   }

   public abstract <T> void set(int var1, T var2);

   public abstract void setAll(Object... var1);
}
