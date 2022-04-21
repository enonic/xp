package com.enonic.xp.impl.map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.core.internal.Local;
import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;

@Component(immediate = true)
@Local
public class LocalSharedMapService
    implements SharedMapService
{
    private final ConcurrentMap<String, SharedMap<?, ?>> sharedMaps = new ConcurrentHashMap<>();

    @Override
    public <K, V> SharedMap<K, V> getSharedMap( final String name )
    {
        return castSharedMap( sharedMaps.computeIfAbsent( name, k -> new LocalSharedMap<>() ) );
    }

    @SuppressWarnings("unchecked")
    private static <K, V> SharedMap<K, V> castSharedMap( final SharedMap<?, ?> anySharedMap )
    {
        return (SharedMap<K, V>) anySharedMap;
    }
}
