package com.enonic.xp.page;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class PageDescriptors
    extends AbstractImmutableEntityList<PageDescriptor>
{
    private static final PageDescriptors EMPTY = new PageDescriptors( ImmutableList.of() );

    private PageDescriptors( final ImmutableList<PageDescriptor> list )
    {
        super( list );
    }

    public static PageDescriptors empty()
    {
        return EMPTY;
    }

    public static PageDescriptors from( final PageDescriptor... pageDescriptors )
    {
        return fromInternal( ImmutableList.copyOf( pageDescriptors ) );
    }

    public static PageDescriptors from( final Iterable<? extends PageDescriptor> pageDescriptors )
    {
        return pageDescriptors instanceof PageDescriptors p ? p : fromInternal( ImmutableList.copyOf( pageDescriptors ) );
    }

    private static PageDescriptors fromInternal( final ImmutableList<PageDescriptor> list )
    {
        return list.isEmpty() ? EMPTY : new PageDescriptors( list );
    }
}
