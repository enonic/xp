package com.enonic.xp.content;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class ExtraDatas
    extends AbstractImmutableEntitySet<ExtraData>
{
    private final ImmutableMap<MixinName, ExtraData> map;

    private ExtraDatas( final Set<ExtraData> set )
    {
        super( ImmutableSet.copyOf( set ) );
        this.map = Maps.uniqueIndex( set, new ToNameFunction() );
    }

    public MixinNames getNames()
    {
        final Collection<MixinName> names = Collections2.transform( this.set, new ToNameFunction() );
        return MixinNames.from( names );
    }

    public ExtraData getMetadata( final MixinName name )
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

    public static ExtraDatas from( final ExtraDatas extraDatas, final ExtraData extraData )
    {
        ImmutableSet.Builder<ExtraData> builder = ImmutableSet.builder();
        builder.addAll( extraDatas );
        builder.add( extraData );
        return new ExtraDatas( builder.build() );
    }

    private final static class ToNameFunction
        implements Function<ExtraData, MixinName>
    {
        @Override
        public MixinName apply( final ExtraData value )
        {
            return value.getName();
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Set<ExtraData> set = Sets.newLinkedHashSet();

        public Builder add( final ExtraData value )
        {
            set.add( value );
            return this;
        }

        public ExtraDatas build()
        {
            return new ExtraDatas( set );
        }
    }
}
