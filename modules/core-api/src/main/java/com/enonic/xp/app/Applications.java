package com.enonic.xp.app;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class Applications
    extends AbstractImmutableEntityList<Application>
{
    private final ImmutableMap<ApplicationKey, Application> map;

    private Applications( final ImmutableList<Application> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToKeyFunction() );
    }

    public ApplicationKeys getApplicationKeys()
    {
        return ApplicationKeys.from( map.keySet() );
    }

    public Application getModule( final ApplicationKey ApplicationKey )
    {
        return map.get( ApplicationKey );
    }

    @Override
    public String toString()
    {
        return this.list.toString();
    }

    public static Applications empty()
    {
        final ImmutableList<Application> list = ImmutableList.of();
        return new Applications( list );
    }

    public static Applications from( final Application... applications )
    {
        return new Applications( ImmutableList.copyOf( applications ) );
    }

    public static Applications from( final Iterable<? extends Application> modules )
    {
        return new Applications( ImmutableList.copyOf( modules ) );
    }

    public static Applications from( final Collection<? extends Application> modules )
    {
        return new Applications( ImmutableList.copyOf( modules ) );
    }

    private final static class ToKeyFunction
        implements Function<Application, ApplicationKey>
    {
        @Override
        public ApplicationKey apply( final Application value )
        {
            return value.getKey();
        }
    }


}
