package com.enonic.xp.style;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class StyleDescriptors
    extends AbstractImmutableEntityList<StyleDescriptor>
{
    private static final StyleDescriptors EMPTY = new StyleDescriptors( ImmutableList.of() );

    private StyleDescriptors( final ImmutableList<StyleDescriptor> list )
    {
        super( list );
    }

    public static StyleDescriptors empty()
    {
        return EMPTY;
    }

    public static StyleDescriptors from( final StyleDescriptor... styleDescriptors )
    {
        return fromInternal( ImmutableList.copyOf( styleDescriptors ) );
    }

    public static StyleDescriptors from( final Iterable<? extends StyleDescriptor> styleDescriptors )
    {
        return styleDescriptors instanceof StyleDescriptors s ? s : fromInternal( ImmutableList.copyOf( styleDescriptors ) );
    }

    public static Collector<StyleDescriptor, ?, StyleDescriptors> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), StyleDescriptors::fromInternal );
    }

    private static StyleDescriptors fromInternal( final ImmutableList<StyleDescriptor> list )
    {
        return list.isEmpty() ? EMPTY : new StyleDescriptors( list );
    }
}
