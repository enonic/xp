package com.enonic.xp.content;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.xdata.MixinName;
import com.enonic.xp.schema.xdata.MixinNames;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class Mixins
    extends AbstractImmutableEntityList<Mixin>
{
    private static final Mixins EMPTY = new Mixins( ImmutableMap.of() );

    private final ImmutableMap<MixinName, Mixin> map;

    private Mixins( final ImmutableMap<MixinName, Mixin> map )
    {
        super( map.values().asList() );
        this.map = map;
    }

    public MixinNames getNames()
    {
        return MixinNames.from( map.keySet() );
    }

    public Mixin getMetadata( final MixinName name )
    {
        return map.get( name );
    }

    public Mixins copy()
    {
        return isEmpty() ? EMPTY : stream().map( Mixin::copy ).collect( collector() );
    }

    public static Mixins empty()
    {
        return EMPTY;
    }

    public static Mixins from( final Iterable<Mixin> extradatas )
    {
        return extradatas instanceof Mixins e ? e : checkDistinct( ImmutableList.copyOf( extradatas ) );
    }

    public static Collector<Mixin, ?, Mixins> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), Mixins::checkDistinct );
    }

    private static Mixins fromInternal( final ImmutableMap<MixinName, Mixin> map )
    {
        return map.isEmpty() ? EMPTY : new Mixins( map );
    }

    private static Mixins checkDistinct( final ImmutableList<Mixin> list )
    {
        return fromInternal( list.stream().collect( ImmutableMap.toImmutableMap( Mixin::getName, Function.identity() ) ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableMap.Builder<MixinName, Mixin> map = ImmutableMap.builder();

        private Builder()
        {
        }

        public Builder add( final Mixin value )
        {
            map.put( value.getName(), value );
            return this;
        }

        public Builder addAll( final Iterable<Mixin> value )
        {
            for ( Mixin extraData : value )
            {
                map.put( extraData.getName(), extraData );
            }
            return this;
        }

        public Mixins build()
        {
            return fromInternal( map.buildOrThrow() );
        }

        public Mixins buildKeepingLast()
        {
            return fromInternal( map.buildKeepingLast() );
        }
    }
}
