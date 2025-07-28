package com.enonic.xp.schema.xdata;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class XDatas
    extends AbstractImmutableEntityList<XData>
{
    private static final XDatas EMPTY = new XDatas( ImmutableList.of() );

    private XDatas( final ImmutableList<XData> list )
    {
        super( list );
    }

    public XDatas add( final XData... xDatas )
    {
        return add( Arrays.asList( xDatas ) );
    }

    public XDatas add( final Iterable<XData> xDatas )
    {
        return fromInternal( ImmutableList.<XData>builder().addAll( this.list ).addAll( xDatas ).build() );
    }

    public XDataNames getNames()
    {
        return this.list.stream().map( BaseSchema::getName ).collect( XDataNames.collector() );
    }

    public XData getXData( final XDataName xDataName )
    {
        return list.stream().filter( x -> xDataName.equals( x.getName() ) ).findFirst().orElse( null );
    }

    public XDatas filter( final Predicate<XData> filter )
    {
        return this.list.stream().filter( filter ).collect( collector() );
    }

    public static XDatas empty()
    {
        return EMPTY;
    }

    public static XDatas from( final XData... xDatas )
    {
        return fromInternal( ImmutableList.copyOf( xDatas ) );
    }

    public static XDatas from( final Iterable<XData> xDatas )
    {
        return xDatas instanceof XDatas x ? x : fromInternal( ImmutableList.copyOf( xDatas ) );
    }

    public static Collector<XData, ?, XDatas> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), XDatas::fromInternal );
    }

    private static XDatas fromInternal( final ImmutableList<XData> list )
    {
        return list.isEmpty() ? EMPTY : new XDatas( list );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<XData> builder = ImmutableList.builder();

        public Builder add( XData node )
        {
            builder.add( node );
            return this;
        }

        public Builder addAll( Iterable<XData> nodes )
        {
            builder.addAll( nodes );
            return this;
        }

        public XDatas build()
        {
            return fromInternal( builder.build() );
        }
    }
}
