package com.enonic.xp.core.impl.security;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.enonic.xp.shared.SharedMap;

/**
 * A cluster-wide lock on a key of a shared map: the body runs while no other node runs a body for the same key.
 */
public final class SharedLocks
{
    private SharedLocks()
    {
    }

    public static <T> T withLock( final SharedMap<String, Boolean> locks, final String key, final Supplier<T> body )
    {
        final List<T> holder = new ArrayList<>( 1 );
        locks.modify( key, _ -> {
            holder.add( body.get() );
            return null;
        } );
        return holder.getFirst();
    }
}
