package com.enonic.xp.schema.xdata;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class XDatas
    extends AbstractImmutableEntityList<XData>
{
    private final ImmutableMap<XDataName, XData> map;

    private XDatas( final ImmutableList<XData> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, XData::getName );
    }

    public XDatas add( final XData... xDatas )
    {
        return add( ImmutableList.copyOf( xDatas ) );
    }

    public XDatas add( final Iterable<XData> xDatas )
    {
        return add( ImmutableList.copyOf( xDatas ) );
    }

    private XDatas add( final ImmutableList<XData> xDatas )
    {
        final List<XData> tmp = Lists.newArrayList();
        tmp.addAll( this.list );
        tmp.addAll( xDatas );

        return new XDatas( ImmutableList.copyOf( tmp ) );
    }

    public Set<XDataName> getNames()
    {
        return ImmutableSet.copyOf( this.list.stream().map( BaseSchema::getName ).iterator() );
    }

    public XData getXData( final XDataName xDataName )
    {
        return map.get( xDataName );
    }

    public XDatas filter( final Predicate<XData> filter )
    {
        return from( this.map.values().stream().filter( filter ).iterator() );
    }

    public static XDatas empty()
    {
        final ImmutableList<XData> list = ImmutableList.of();
        return new XDatas( list );
    }

    public static XDatas from( final XData... xDatas )
    {
        return new XDatas( ImmutableList.copyOf( xDatas ) );
    }

    public static XDatas from( final Iterable<? extends XData> xDatas )
    {
        return new XDatas( ImmutableList.copyOf( xDatas ) );
    }

    public static XDatas from( final Iterator<? extends XData> xDatas )
    {
        return new XDatas( ImmutableList.copyOf( xDatas ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<XData> builder = ImmutableList.builder();

        public Builder add( XData node )
        {
            builder.add( node );
            return this;
        }

        public Builder addAll( XDatas nodes )
        {
            builder.addAll( nodes );
            return this;
        }

        public Builder addAll( Collection<XData> nodes )
        {
            builder.addAll( nodes );
            return this;
        }

        public XDatas build()
        {
            return new XDatas( builder.build() );
        }
    }
}
