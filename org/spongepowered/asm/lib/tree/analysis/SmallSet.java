package org.spongepowered.asm.lib.tree.analysis;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

class SmallSet<E> extends AbstractSet<E> implements Iterator<E> {
   E e1;
   E e2;

   static final <T> Set<T> emptySet() {
      return new SmallSet((Object)null, (Object)null);
   }

   SmallSet(E var1, E var2) {
      this.e1 = var1;
      this.e2 = var2;
   }

   public Iterator<E> iterator() {
      return new SmallSet(this.e1, this.e2);
   }

   public int size() {
      return this.e1 == null ? 0 : (this.e2 == null ? 1 : 2);
   }

   public boolean hasNext() {
      return this.e1 != null;
   }

   public E next() {
      if (this.e1 == null) {
         throw new NoSuchElementException();
      } else {
         Object var1 = this.e1;
         this.e1 = this.e2;
         this.e2 = null;
         return var1;
      }
   }

   public void remove() {
   }

   Set<E> union(SmallSet<E> var1) {
      if ((var1.e1 != this.e1 || var1.e2 != this.e2) && (var1.e1 != this.e2 || var1.e2 != this.e1)) {
         if (var1.e1 == null) {
            return this;
         } else if (this.e1 == null) {
            return var1;
         } else {
            if (var1.e2 == null) {
               if (this.e2 == null) {
                  return new SmallSet(this.e1, var1.e1);
               }

               if (var1.e1 == this.e1 || var1.e1 == this.e2) {
                  return this;
               }
            }

            if (this.e2 != null || this.e1 != var1.e1 && this.e1 != var1.e2) {
               HashSet var2 = new HashSet(4);
               var2.add(this.e1);
               if (this.e2 != null) {
                  var2.add(this.e2);
               }

               var2.add(var1.e1);
               if (var1.e2 != null) {
                  var2.add(var1.e2);
               }

               return var2;
            } else {
               return var1;
            }
         }
      } else {
         return this;
      }
   }
}
