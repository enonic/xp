package com.enonic.xp.app;

import java.util.Arrays;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class ApplicationKeys
    extends AbstractImmutableEntityList<ApplicationKey>
{
    private ApplicationKeys( final ImmutableList<ApplicationKey> list )
    {
        super( list );
    }

    public static ApplicationKeys from( final ApplicationKey... applicationKeys )
    {
        return new ApplicationKeys( ImmutableList.copyOf( applicationKeys ) );
    }

    public static ApplicationKeys from( final Iterable<? extends ApplicationKey> applicationKeys )
    {
        return new ApplicationKeys( ImmutableList.copyOf( applicationKeys ) );
    }

    public static ApplicationKeys from( final String... applicationKeys )
    {
        return new ApplicationKeys( parseApplicationKeys( applicationKeys ) );
    }

    public static ApplicationKeys empty()
    {
        return new ApplicationKeys( ImmutableList.<ApplicationKey>of() );
    }

    private static ImmutableList<ApplicationKey> parseApplicationKeys( final String... applicationKeys )
    {
        final ApplicationKey[] applicationKeyList = Arrays.stream( applicationKeys ).
            map( ApplicationKey::from ).
            toArray( ApplicationKey[]::new );
        return ImmutableList.copyOf( applicationKeyList );
    }

}
