package com.enonic.xp.support;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

@Beta
public abstract class AbstractImmutableEntityList<T>
    implements Iterable<T>
{
    protected final ImmutableList<T> list;

    protected AbstractImmutableEntityList( final ImmutableList<T> list )
    {
        this.list = list;
    }

    public final int getSize()
    {
        return this.list.size();
    }

    public final boolean isEmpty()
    {
        return this.list.isEmpty();
    }

    public final boolean isNotEmpty()
    {
        return !this.list.isEmpty();
    }

    public final T get( final int index )
    {
        return this.list.get( index );
    }

    public final T first()
    {
        return this.list.isEmpty() ? null : this.list.get( 0 );
    }

    public final T last()
    {
        return this.list.isEmpty() ? null : this.list.get( this.list.size() - 1 );
    }

    public final List<T> getList()
    {
        return this.list;
    }

    public final Stream<T> stream()
    {
        return this.list.stream();
    }

    public final boolean contains( T o )
    {
        return this.list.contains( o );
    }

    @Override
    public final Iterator<T> iterator()
    {
        return this.list.iterator();
    }

    @Override
    public String toString()
    {
        return this.list.toString();
    }

    @Override
    public int hashCode()
    {
        return this.list.hashCode();
    }

    @Override
    public boolean equals( final Object o )
    {
        return this.getClass().isInstance( o ) && this.list.equals( ( (AbstractImmutableEntityList) o ).list );
    }
}
