package com.enonic.xp.content;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataNames;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ExtraDatas
    extends AbstractImmutableEntityList<ExtraData>
{
    private static final ExtraDatas EMPTY = new ExtraDatas( ImmutableList.of() );

    private ExtraDatas( final ImmutableList<ExtraData> list )
    {
        super( list );
    }

    public XDataNames getNames()
    {
        return list.stream().map( ExtraData::getName ).collect( XDataNames.collector() );
    }

    public ExtraData getMetadata( final XDataName name )
    {
        return list.stream().filter( xd ->  name.equals( xd.getName() ) ).findFirst().orElse( null );
    }

    public ExtraDatas copy()
    {
        return fromInternal( this.list.stream().map( ExtraData::copy ).collect( ImmutableList.toImmutableList() ) );
    }

    public static ExtraDatas empty()
    {
        return EMPTY;
    }

    public static ExtraDatas from( final Iterable<ExtraData> extradatas )
    {
        return extradatas instanceof ExtraDatas e ? e : new ExtraDatas( ImmutableList.copyOf( extradatas ) );
    }

    public static Collector<ExtraData, ?, ExtraDatas> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), ExtraDatas::fromInternal );
    }

    private static ExtraDatas fromInternal( final ImmutableList<ExtraData> list )
    {
        return list.isEmpty() ? EMPTY : new ExtraDatas( list );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ExtraData> list = ImmutableList.builder();

        public Builder add( final ExtraData value )
        {
            list.add( value );
            return this;
        }

        public Builder addAll( final Iterable<ExtraData> value )
        {
            list.addAll( value );
            return this;
        }

        public ExtraDatas build()
        {
            return fromInternal( list.build() );
        }
    }
}
