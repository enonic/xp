package com.enonic.xp.site;

import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.xdata.XDataNames;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class XDataMappings
    extends AbstractImmutableEntityList<XDataMapping>
{
    private static final XDataMappings EMPTY = new XDataMappings( ImmutableList.of() );

    private XDataMappings( final ImmutableList<XDataMapping> list )
    {
        super( list );
    }

    public static XDataMappings empty()
    {
        return EMPTY;
    }

    public static XDataMappings from( final XDataMapping... xDataMappings )
    {
        return fromInternal( ImmutableList.copyOf( xDataMappings ) );
    }

    public static XDataMappings from( final Iterable<? extends XDataMapping> xDataMappings )
    {
        return fromInternal( ImmutableList.copyOf( xDataMappings ) );
    }

    public static XDataMappings from( final Collection<? extends XDataMapping> xDataMappings )
    {
        return fromInternal( ImmutableList.copyOf( xDataMappings ) );
    }

    public static XDataMappings fromInternal( final ImmutableList<XDataMapping> xDataMappings )
    {
        return xDataMappings.isEmpty() ? EMPTY : new XDataMappings( xDataMappings );
    }

    public static Collector<XDataMapping, ?, XDataMappings> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), XDataMappings::fromInternal );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public XDataNames getNames()
    {
        return XDataNames.from( this.stream().map( XDataMapping::getXDataName ).collect( Collectors.toList() ) );
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<XDataMapping> builder = ImmutableList.builder();

        public XDataMappings.Builder add( XDataMapping xDataMapping )
        {
            builder.add( xDataMapping );
            return this;
        }

        public XDataMappings.Builder addAll( Iterable<? extends XDataMapping> xDataMappings )
        {
            builder.addAll( xDataMappings );
            return this;
        }

        public XDataMappings build()
        {
            return new XDataMappings( builder.build() );
        }
    }
}
