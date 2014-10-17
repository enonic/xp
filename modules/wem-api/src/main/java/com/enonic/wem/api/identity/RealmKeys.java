package com.enonic.wem.api.identity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

import static java.util.stream.Collectors.toList;

public final class RealmKeys
    extends AbstractImmutableEntityList<RealmKey>
{
    private RealmKeys( final ImmutableList<RealmKey> list )
    {
        super( list );
    }

    public static RealmKeys from( final RealmKey... realmKeys )
    {
        return new RealmKeys( ImmutableList.copyOf( realmKeys ) );
    }

    public static RealmKeys from( final Iterable<? extends RealmKey> realmKeys )
    {
        return new RealmKeys( ImmutableList.copyOf( realmKeys ) );
    }

    public static RealmKeys from( final Collection<? extends RealmKey> realmKeys )
    {
        return new RealmKeys( ImmutableList.copyOf( realmKeys ) );
    }

    public static RealmKeys from( final String... realmKeys )
    {
        return new RealmKeys( parseRealmKeys( realmKeys ) );
    }

    public static RealmKeys empty()
    {
        return new RealmKeys( ImmutableList.<RealmKey>of() );
    }

    private static ImmutableList<RealmKey> parseRealmKeys( final String... realmKeys )
    {
        final List<RealmKey> realmKeyList = Stream.of( realmKeys ).map( RealmKey::new ).collect( toList() );
        return ImmutableList.copyOf( realmKeyList );
    }
}
