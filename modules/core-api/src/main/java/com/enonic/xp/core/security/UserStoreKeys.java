package com.enonic.xp.core.security;

import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.core.support.AbstractImmutableEntityList;

import static java.util.stream.Collectors.toList;

public final class UserStoreKeys
    extends AbstractImmutableEntityList<UserStoreKey>
{
    private UserStoreKeys( final ImmutableList<UserStoreKey> list )
    {
        super( list );
    }

    public static UserStoreKeys from( final UserStoreKey... userStoreKeys )
    {
        return new UserStoreKeys( ImmutableList.copyOf( userStoreKeys ) );
    }

    public static UserStoreKeys from( final Iterable<? extends UserStoreKey> userStoreKeys )
    {
        return new UserStoreKeys( ImmutableList.copyOf( userStoreKeys ) );
    }

    public static UserStoreKeys from( final String... userStoreKeys )
    {
        return new UserStoreKeys( parseUserStoreKeys( userStoreKeys ) );
    }

    public static UserStoreKeys empty()
    {
        return new UserStoreKeys( ImmutableList.<UserStoreKey>of() );
    }

    private static ImmutableList<UserStoreKey> parseUserStoreKeys( final String... userStoreKeys )
    {
        final List<UserStoreKey> userStoreKeyList = Stream.of( userStoreKeys ).map( UserStoreKey::new ).collect( toList() );
        return ImmutableList.copyOf( userStoreKeyList );
    }
}
