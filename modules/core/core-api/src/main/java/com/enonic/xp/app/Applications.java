package com.enonic.xp.app;

import java.util.Collection;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class Applications
    extends AbstractImmutableEntityList<Application>
{
    private final ImmutableMap<ApplicationKey, Application> map;

    private Applications( final ImmutableList<Application> list )
    {
        super( list );
        this.map = list.stream().collect( ImmutableMap.toImmutableMap( Application::getKey, Function.identity() ) );
    }

    public ApplicationKeys getApplicationKeys()
    {
        return ApplicationKeys.from( map.keySet() );
    }

    public Application getApplication( final ApplicationKey applicationKey )
    {
        return map.get( applicationKey );
    }

    @Override
    public String toString()
    {
        return this.list.toString();
    }

    public static Applications empty()
    {
        return new Applications( ImmutableList.of() );
    }

    public static Applications from( final Application... applications )
    {
        return new Applications( ImmutableList.copyOf( applications ) );
    }

    public static Applications from( final Iterable<? extends Application> applications )
    {
        return new Applications( ImmutableList.copyOf( applications ) );
    }

    public static Applications from( final Collection<? extends Application> applications )
    {
        return new Applications( ImmutableList.copyOf( applications ) );
    }
}
