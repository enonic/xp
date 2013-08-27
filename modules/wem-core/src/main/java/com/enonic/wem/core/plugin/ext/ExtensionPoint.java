package com.enonic.wem.core.plugin.ext;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.wem.api.plugin.ext.Extension;


public abstract class ExtensionPoint<T extends Extension>
    extends FilteredExtensionListener<T>
    implements Iterable<T>, Comparator<T>
{
    private ImmutableList<T> list;

    public ExtensionPoint( final Class<T> type )
    {
        super( type );
        this.list = ImmutableList.of();
    }

    public final String getName()
    {
        return getType().getSimpleName();
    }

    public final boolean isEmpty()
    {
        return this.list.isEmpty();
    }

    @Override
    public final Iterator<T> iterator()
    {
        return this.list.iterator();
    }

    protected synchronized final void addExtension( final T ext )
    {
        final List<T> other = Lists.newArrayList( this.list );
        other.add( ext );
        Collections.sort( other, this );
        this.list = ImmutableList.copyOf( other );
    }

    protected synchronized final void removeExtension( final T ext )
    {
        final List<T> other = Lists.newArrayList( this.list );
        other.remove( ext );
        Collections.sort( other, this );
        this.list = ImmutableList.copyOf( other );
    }
}
