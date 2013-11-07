package com.enonic.wem.core.hazelcast;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.multibindings.MapBinder;
import com.hazelcast.nio.serialization.StreamSerializer;

public final class HazelcastBinder
{
    private final MapBinder<Class, StreamSerializer> streamSerializers;

    private HazelcastBinder( final Binder binder )
    {
        this.streamSerializers = MapBinder.newMapBinder( binder, Class.class, StreamSerializer.class );
    }

    public <T, S extends StreamSerializer<T>> void addSerializer( Class<T> type, Class<S> serializer )
    {
        this.streamSerializers.addBinding( type ).to( serializer ).in( Scopes.SINGLETON );
    }

    public static HazelcastBinder from( final Binder binder )
    {
        return new HazelcastBinder( binder );
    }
}
