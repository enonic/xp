package com.enonic.xp.content;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataNames;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class ExtraDatas
    extends AbstractImmutableEntitySet<ExtraData>
{
    private final ImmutableMap<XDataName, ExtraData> map;

    private ExtraDatas( final Set<ExtraData> set )
    {
        super( ImmutableSet.copyOf( set ) );
        this.map = set.stream().collect( ImmutableMap.toImmutableMap( ExtraData::getName, Function.identity() ) );
    }

    public XDataNames getNames()
    {
        return XDataNames.from( map.keySet() );
    }

    public ExtraData getMetadata( final XDataName name )
    {
        return this.map.get( name );
    }

    public ExtraDatas copy()
    {
        return ExtraDatas.from( this.map.values().stream().map( ExtraData::copy ).collect( Collectors.toList() ) );
    }

    public static ExtraDatas empty()
    {
        final ImmutableSet<ExtraData> set = ImmutableSet.of();
        return new ExtraDatas( set );
    }

    public static ExtraDatas from( final Iterable<? extends ExtraData> extradatas )
    {
        return new ExtraDatas( ImmutableSet.copyOf( extradatas ) );
    }

    public static ExtraDatas from( final Stream<? extends ExtraData> extradatas )
    {
        return new ExtraDatas( ImmutableSet.copyOf( extradatas.collect( Collectors.toSet() ) ) );
    }

    public static ExtraDatas from( final ExtraDatas extraDatas, final ExtraData extraData )
    {
        ImmutableSet.Builder<ExtraData> builder = ImmutableSet.builder();
        builder.addAll( extraDatas );
        builder.add( extraData );
        return new ExtraDatas( builder.build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Set<ExtraData> set = new LinkedHashSet<>();

        public Builder add( final ExtraData value )
        {
            set.add( value );
            return this;
        }

        public Builder addAll( final Collection<ExtraData> value )
        {
            set.addAll( value );
            return this;
        }

        public ExtraDatas build()
        {
            return new ExtraDatas( set );
        }
    }
}
