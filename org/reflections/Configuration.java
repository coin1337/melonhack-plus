package org.reflections;

import java.net.URL;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;
import org.reflections.adapters.MetadataAdapter;
import org.reflections.scanners.Scanner;
import org.reflections.serializers.Serializer;

public interface Configuration {
   Set<Scanner> getScanners();

   Set<URL> getUrls();

   MetadataAdapter getMetadataAdapter();

   Predicate<String> getInputsFilter();

   ExecutorService getExecutorService();

   Serializer getSerializer();

   ClassLoader[] getClassLoaders();

   boolean shouldExpandSuperTypes();
}
