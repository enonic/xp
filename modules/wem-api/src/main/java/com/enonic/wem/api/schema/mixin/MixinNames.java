package com.enonic.wem.api.schema.mixin;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public final class MixinNames
    extends AbstractImmutableEntitySet<MixinName>
{
    private MixinNames( final ImmutableSortedSet<MixinName> set )
    {
        super( set );
    }

    public static MixinNames empty()
    {
        final ImmutableSortedSet<MixinName> set = ImmutableSortedSet.of();
        return new MixinNames( set );
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
        return new MixinNames( ImmutableSortedSet.copyOf( mixinNames ) );
    }

    public static MixinNames from( final Iterable<MixinName> mixinNames )
    {
        return new MixinNames( ImmutableSortedSet.copyOf( mixinNames ) );
    }

    private static ImmutableSortedSet<MixinName> parseQualifiedNames( final String... mixinNames )
    {
        final Collection<String> list = Lists.newArrayList( mixinNames );
        final Collection<MixinName> mixinNameList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSortedSet.copyOf( mixinNameList );
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
