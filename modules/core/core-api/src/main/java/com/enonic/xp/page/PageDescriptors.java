package com.enonic.xp.page;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class PageDescriptors
    extends AbstractImmutableEntityList<PageDescriptor>
{
    private PageDescriptors( final ImmutableList<PageDescriptor> list )
    {
        super( list );
    }

    public static PageDescriptors empty()
    {
        return new PageDescriptors( ImmutableList.of() );
    }

    public static PageDescriptors from( final PageDescriptor... pageDescriptors )
    {
        return pageDescriptors != null ? new PageDescriptors( ImmutableList.copyOf( pageDescriptors ) ) : PageDescriptors.empty();
    }

    public static PageDescriptors from( final Iterable<? extends PageDescriptor> pageDescriptors )
    {
        return pageDescriptors != null ? new PageDescriptors( ImmutableList.copyOf( pageDescriptors ) ) : PageDescriptors.empty();
    }

    public static PageDescriptors from( final Collection<? extends PageDescriptor> pageDescriptors )
    {
        return pageDescriptors != null ? new PageDescriptors( ImmutableList.copyOf( pageDescriptors ) ) : PageDescriptors.empty();
    }
}
