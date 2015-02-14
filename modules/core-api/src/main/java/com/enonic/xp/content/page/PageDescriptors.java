package com.enonic.xp.content.page;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class PageDescriptors
    extends AbstractImmutableEntityList<PageDescriptor>
{

    private PageDescriptors( final ImmutableList<PageDescriptor> list )
    {
        super( list );
    }

    public static PageDescriptors empty()
    {
        final ImmutableList<PageDescriptor> list = ImmutableList.of();
        return new PageDescriptors( list );
    }

    public static PageDescriptors from( final PageDescriptor... pageDescriptors )
    {
        return new PageDescriptors( ImmutableList.copyOf( pageDescriptors ) );
    }

    public static PageDescriptors from( final Iterable<? extends PageDescriptor> pageDescriptors )
    {
        return new PageDescriptors( ImmutableList.copyOf( pageDescriptors ) );
    }

    public static PageDescriptors from( final Collection<? extends PageDescriptor> pageDescriptors )
    {
        return new PageDescriptors( ImmutableList.copyOf( pageDescriptors ) );
    }

}
