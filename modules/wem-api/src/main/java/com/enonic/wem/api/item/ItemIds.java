package com.enonic.wem.api.item;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public class ItemIds
    extends AbstractImmutableEntitySet<ItemId>
    implements ItemSelectors<ItemId>
{

    private ItemIds( final ImmutableSet<ItemId> set )
    {
        super( set );
    }

    public static ItemIds empty()
    {
        final ImmutableSet<ItemId> set = ImmutableSet.of();
        return new ItemIds( set );
    }

    public static ItemIds from( final ItemId... ids )
    {
        return new ItemIds( ImmutableSet.copyOf( ids ) );
    }

    public static ItemIds from( final String... ids )
    {
        return new ItemIds( parseIds( ids ) );
    }

    public static ItemIds from( final Iterable<ItemId> ids )
    {
        return new ItemIds( ImmutableSet.copyOf( ids ) );
    }

    private static ImmutableSet<ItemId> parseIds( final String... paths )
    {
        final Collection<String> list = Lists.newArrayList( paths );
        final Collection<ItemId> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }

    private final static class ParseFunction
        implements Function<String, ItemId>
    {
        @Override
        public ItemId apply( final String value )
        {
            return ItemId.from( value );
        }
    }
}
