package com.enonic.xp.style;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class StyleDescriptors
    extends AbstractImmutableEntityList<StyleDescriptor>
{
    private StyleDescriptors( final ImmutableList<StyleDescriptor> list )
    {
        super( list );
    }

    public static StyleDescriptors from( final StyleDescriptor... styleDescriptors )
    {
        return new StyleDescriptors( ImmutableList.copyOf( styleDescriptors ) );
    }

    public static StyleDescriptors from( final Collection<StyleDescriptor> styleDescriptors )
    {
        return new StyleDescriptors( ImmutableList.copyOf( styleDescriptors ) );
    }

    public static StyleDescriptors empty()
    {
        return new StyleDescriptors( ImmutableList.of() );
    }
}
