package com.enonic.xp.schema.mixin;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class MixinNames
    extends AbstractImmutableEntityList<MixinName>
{
    private MixinNames( final ImmutableList<MixinName> list )
    {
        super( list );
    }

    public static MixinNames empty()
    {
        return new MixinNames( ImmutableList.of() );
    }

    public static MixinNames from( final String... mixinNames )
    {
        return new MixinNames( parseQualifiedNames( mixinNames ) );
    }

    public static MixinNames from( final Collection<String> mixinNames )
    {
        return from( mixinNames.toArray( new String[mixinNames.size()] ) );
    }

    public static MixinNames from( final MixinName... mixinNames )
    {
        return new MixinNames( ImmutableList.copyOf( mixinNames ) );
    }

    public static MixinNames from( final Iterable<MixinName> mixinNames )
    {
        return new MixinNames( ImmutableList.copyOf( mixinNames ) );
    }

    private static ImmutableList<MixinName> parseQualifiedNames( final String... mixinNames )
    {
        return Arrays.stream( mixinNames ).map( MixinName::from ).collect( ImmutableList.toImmutableList() );
    }
}
