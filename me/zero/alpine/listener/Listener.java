package me.zero.alpine.listener;

import java.util.function.Predicate;

public final class Listener<T> implements EventHook<T> {
   private final Class<T> target;
   private final EventHook<T> hook;
   private final Predicate<T>[] filters;
   private final byte priority;

   @SafeVarargs
   public Listener(EventHook<T> param1, Predicate<T>... param2) {
      // $FF: Couldn't be decompiled
   }

   @SafeVarargs
   public Listener(EventHook<T> param1, byte param2, Predicate<T>... param3) {
      // $FF: Couldn't be decompiled
   }

   public final Class<T> getTarget() {
      // $FF: Couldn't be decompiled
   }

   public final byte getPriority() {
      // $FF: Couldn't be decompiled
   }

   public final void invoke(T param1) {
      // $FF: Couldn't be decompiled
   }
}
