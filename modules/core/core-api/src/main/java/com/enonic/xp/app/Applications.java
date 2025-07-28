package com.enonic.xp.app;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class Applications
    extends AbstractImmutableEntityList<Application>
{
    private static final Applications EMPTY = new Applications( ImmutableList.of() );

    private Applications( final ImmutableList<Application> list )
    {
        super( list );
    }

    public ApplicationKeys getApplicationKeys()
    {
        return list.stream().map( Application::getKey ).collect( ApplicationKeys.collector() );
    }

    public static Applications empty()
    {
        return EMPTY;
    }

    public static Applications from( final Application... applications )
    {
        return fromInternal( ImmutableList.copyOf( applications ) );
    }

    public static Applications from( final Iterable<Application> applications )
    {
        return applications instanceof Applications a ? a : fromInternal( ImmutableList.copyOf( applications ) );
    }

    private static Applications fromInternal( ImmutableList<Application> list )
    {
        return list.isEmpty() ? EMPTY : new Applications( list );
    }
}
