package com.enonic.xp.schema.mixin;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class MixinNames
    extends AbstractImmutableEntityList<MixinName>
{
    private MixinNames( final ImmutableList<MixinName> list )
    {
        super( list );
    }

    public static MixinNames empty()
    {
        final ImmutableList<MixinName> list = ImmutableList.of();
        return new MixinNames( list );
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
        final Collection<String> list = Lists.newArrayList( mixinNames );
        final Collection<MixinName> mixinNameList = Collections2.transform( list, new ParseFunction() );
        return ImmutableList.copyOf( mixinNameList );
    }

    private final static class ParseFunction
        implements Function<String, MixinName>
    {
        @Override
        public MixinName apply( final String value )
        {
            return MixinName.from( value );
        }
    }

}
