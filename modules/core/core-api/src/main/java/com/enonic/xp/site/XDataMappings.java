package com.enonic.xp.site;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.xdata.XDataNames;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public class XDataMappings
    extends AbstractImmutableEntityList<XDataMapping>
{
    private XDataMappings( final ImmutableList<XDataMapping> list )
    {
        super( list );
    }

    public static XDataMappings empty()
    {
        return new XDataMappings( ImmutableList.of() );
    }

    public static XDataMappings from( final XDataMapping... xDataMappings )
    {
        return new XDataMappings( ImmutableList.copyOf( xDataMappings ) );
    }

    public static XDataMappings from( final Iterable<? extends XDataMapping> xDataMappings )
    {
        return new XDataMappings( ImmutableList.copyOf( xDataMappings ) );
    }

    public static XDataMappings from( final Collection<? extends XDataMapping> xDataMappings )
    {
        return new XDataMappings( ImmutableList.copyOf( xDataMappings ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public XDataNames getNames()
    {
        return XDataNames.from( this.stream().map( XDataMapping::getXDataName ).collect( Collectors.toList() ) );
    }

    public static class Builder
    {
        private ImmutableList.Builder<XDataMapping> builder = ImmutableList.builder();

        public XDataMappings.Builder add( XDataMapping xDataMapping )
        {
            builder.add( xDataMapping );
            return this;
        }

        public XDataMappings.Builder addAll( XDataMappings xDataMappings )
        {
            builder.addAll( xDataMappings );
            return this;
        }

        public XDataMappings.Builder addAll( Collection<XDataMapping> xDataMappings )
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
