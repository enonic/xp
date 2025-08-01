package com.enonic.xp.content;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataNames;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ExtraDatas
    extends AbstractImmutableEntityList<ExtraData>
{
    private static final ExtraDatas EMPTY = new ExtraDatas( ImmutableMap.of() );

    private final ImmutableMap<XDataName, ExtraData> map;

    private ExtraDatas( final ImmutableMap<XDataName, ExtraData> map )
    {
        super( map.values().asList() );
        this.map = map;
    }

    public XDataNames getNames()
    {
        return XDataNames.from( map.keySet() );
    }

    public ExtraData getMetadata( final XDataName name )
    {
        return map.get( name );
    }

    public ExtraDatas copy()
    {
        return stream().map( ExtraData::copy ).collect( collector() );
    }

    public static ExtraDatas empty()
    {
        return EMPTY;
    }

    public static ExtraDatas from( final Iterable<ExtraData> extradatas )
    {
        return extradatas instanceof ExtraDatas e ? e : checkDistinct( ImmutableList.copyOf( extradatas ) );
    }

    public static Collector<ExtraData, ?, ExtraDatas> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), ExtraDatas::checkDistinct );
    }

    private static ExtraDatas fromInternal( final ImmutableMap<XDataName, ExtraData> map )
    {
        return map.isEmpty() ? EMPTY : new ExtraDatas( map );
    }

    private static ExtraDatas checkDistinct( final ImmutableList<ExtraData> list )
    {
        return fromInternal( list.stream().collect( ImmutableMap.toImmutableMap( ExtraData::getName, Function.identity() ) ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableMap.Builder<XDataName, ExtraData> map = ImmutableMap.builder();

        private Builder()
        {
        }

        public Builder add( final ExtraData value )
        {
            map.put( value.getName(), value );
            return this;
        }

        public Builder addAll( final Iterable<ExtraData> value )
        {
            for ( ExtraData extraData : value )
            {
                map.put( extraData.getName(), extraData );
            }
            return this;
        }

        public ExtraDatas build()
        {
            return fromInternal( map.buildOrThrow() );
        }

        public ExtraDatas buildKeepingLast()
        {
            return fromInternal( map.buildKeepingLast() );
        }
    }
}
