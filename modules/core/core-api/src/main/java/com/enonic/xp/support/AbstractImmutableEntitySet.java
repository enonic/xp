package com.enonic.xp.support;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class AbstractImmutableEntitySet<T>
    implements Iterable<T>
{
    protected final ImmutableSet<T> set;

    protected AbstractImmutableEntitySet( final ImmutableSet<T> set )
    {
        this.set = set;
    }

    public final int getSize()
    {
        return this.set.size();
    }

    public final boolean isEmpty()
    {
        return this.set.isEmpty();
    }

    public final boolean isNotEmpty()
    {
        return !this.set.isEmpty();
    }

    public final T first()
    {
        return this.set.isEmpty() ? null : this.set.iterator().next();
    }

    public final Set<T> getSet()
    {
        return this.set;
    }

    public final Stream<T> stream()
    {
        return this.set.stream();
    }

    public final boolean contains( T o )
    {
        return this.set.contains( o );
    }

    @Override
    public final Iterator<T> iterator()
    {
        return this.set.iterator();
    }

    @Override
    public String toString()
    {
        return this.set.toString();
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || this.getClass().isInstance( o ) && this.set.equals( ( (AbstractImmutableEntitySet) o ).set );
    }

    @Override
    public int hashCode()
    {
        return this.set.hashCode();
    }
}
