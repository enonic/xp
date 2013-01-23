package com.enonic.wem.api.content.type;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.wem.api.util.AbstractImmutableEntityList;
import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.api.content.type.form.QualifiedMixinName;

public final class Mixins
    extends AbstractImmutableEntityList<Mixin>
{
    private final ImmutableMap<QualifiedMixinName, Mixin> map;

    private Mixins( final ImmutableList<Mixin> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public Set<QualifiedMixinName> getQualifiedMixinNames()
    {
        final Collection<QualifiedMixinName> names = Collections2.transform( this.list, new ToNameFunction() );
        return ImmutableSet.copyOf( names );
    }

    public Mixin getMixin( final QualifiedMixinName qualifiedMixinName )
    {
        return map.get( qualifiedMixinName );
    }

    public static Mixins empty()
    {
        final ImmutableList<Mixin> list = ImmutableList.of();
        return new Mixins( list );
    }

    public static Mixins from( final Mixin... mixins )
    {
        return new Mixins( ImmutableList.copyOf( mixins ) );
    }

    public static Mixins from( final Iterable<? extends Mixin> mixins )
    {
        return new Mixins( ImmutableList.copyOf( mixins ) );
    }

    public static Mixins from( final Collection<? extends Mixin> mixins )
    {
        return new Mixins( ImmutableList.copyOf( mixins ) );
    }

    private final static class ToNameFunction
        implements Function<Mixin, QualifiedMixinName>
    {
        @Override
        public QualifiedMixinName apply( final Mixin value )
        {
            return value.getQualifiedName();
        }
    }
}
