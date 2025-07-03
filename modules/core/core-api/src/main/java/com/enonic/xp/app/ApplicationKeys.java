package com.enonic.xp.app;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class ApplicationKeys
    extends AbstractImmutableEntitySet<ApplicationKey>
{
    private static final ApplicationKeys EMPTY = new ApplicationKeys( ImmutableSet.of() );

    private ApplicationKeys( final ImmutableSet<ApplicationKey> list )
    {
        super( list );
    }

    public static ApplicationKeys from( final ApplicationKey... applicationKeys )
    {
        return fromInternal( ImmutableSet.copyOf( applicationKeys ) );
    }

    public static ApplicationKeys from( final Iterable<? extends ApplicationKey> applicationKeys )
    {
        return fromInternal( ImmutableSet.copyOf( applicationKeys ) );
    }

    public static ApplicationKeys from( final Collection<? extends ApplicationKey> applicationKeys )
    {
        return fromInternal( ImmutableSet.copyOf( applicationKeys ) );
    }

    public static ApplicationKeys from( final String... applicationKeys )
    {
        return fromInternal( Arrays.stream( applicationKeys ).map( ApplicationKey::from ).collect( ImmutableSet.toImmutableSet() ) );
    }

    public static ApplicationKeys empty()
    {
        return EMPTY;
    }

    private static ApplicationKeys fromInternal( final ImmutableSet<ApplicationKey> applicationKeys )
    {
        return applicationKeys.isEmpty() ? EMPTY : new ApplicationKeys( applicationKeys );
    }
}
