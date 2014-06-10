package com.enonic.wem.guice.internal.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Binding;
import com.google.inject.Injector;

public final class InjectorUtils
{
    public static List<Binding<?>> findBindings( final Injector injector, final Class<?> instanceOf )
    {
        final Stream<Binding<?>> bindings = injector.getBindings().values().stream();
        final Stream<Binding<?>> filtered = bindings.filter( b -> instanceOf.isAssignableFrom( b.getKey().getTypeLiteral().getRawType() ) );
        return filtered.collect( Collectors.toList() );
    }
}
