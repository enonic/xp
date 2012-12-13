package com.enonic.wem.api;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableSet;

@Immutable
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

    @Override
    public final Iterator<T> iterator()
    {
        return this.set.iterator();
    }

    public String toString()
    {
        return this.set.toString();
    }

    public int hashCode()
    {
        return this.set.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o != null ) && ( this.getClass().isInstance( o ) ) && this.set.equals( ( (AbstractImmutableEntitySet) o ).set );
    }
}
