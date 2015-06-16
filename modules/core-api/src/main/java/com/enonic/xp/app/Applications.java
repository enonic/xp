package com.enonic.xp.app;

import java.util.function.Predicate;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.support.AbstractImmutableEntityList;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Beta
public final class Applications
    extends AbstractImmutableEntityList<Application>
{
    private final ImmutableMap<ApplicationKey, Application> map;

    private Applications( final ImmutableList<Application> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, Application::getKey );
    }

    public ApplicationKeys getKeys()
    {
        return ApplicationKeys.from( map.keySet() );
    }

    public Application getApplication( final ApplicationKey ApplicationKey )
    {
        return map.get( ApplicationKey );
    }

    public Applications filter( Predicate<Application> predicate )
    {
        final ImmutableList<Application> applicationList = this.list.stream().
            filter( predicate ).
            collect( collectingAndThen( toList(), ImmutableList::copyOf ) );
        return new Applications( applicationList );
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

    public static Applications from( final Iterable<? extends Application> applications )
    {
        return new Applications( ImmutableList.copyOf( applications ) );
    }

}