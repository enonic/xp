package com.enonic.xp.app;

import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    public static ApplicationKeys from( final Iterable<ApplicationKey> applicationKeys )
    {
        return applicationKeys instanceof ApplicationKeys a ? a : fromInternal( ImmutableSet.copyOf( applicationKeys ) );
    }

    public static ApplicationKeys from( final String... applicationKeys )
    {
        return Arrays.stream( applicationKeys ).map( ApplicationKey::from ).collect( collector() );
    }

    public static ApplicationKeys empty()
    {
        return EMPTY;
    }


    public static Collector <ApplicationKey, ?, ApplicationKeys> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), ApplicationKeys::fromInternal );
    }

    private static ApplicationKeys fromInternal( final ImmutableSet<ApplicationKey> applicationKeys )
    {
        return applicationKeys.isEmpty() ? EMPTY : new ApplicationKeys( applicationKeys );
    }
}
