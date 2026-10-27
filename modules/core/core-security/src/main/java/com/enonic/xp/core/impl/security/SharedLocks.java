package com.enonic.xp.core.impl.security;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.shared.SharedMap;

/**
 * A cluster-wide lock on a key of a shared map: the body runs while no other node runs a body for the same key.
 */
@NullMarked
public final class SharedLocks
{
    private SharedLocks()
    {
    }

    public static <T extends @Nullable Object> T withLock( final SharedMap<String, Boolean> locks, final String key, final Supplier<T> body )
    {
        final List<T> holder = new ArrayList<>( 1 );
        locks.modify( key, _ -> {
            holder.add( body.get() );
            return null;
        } );
        return holder.getFirst();
    }
}
