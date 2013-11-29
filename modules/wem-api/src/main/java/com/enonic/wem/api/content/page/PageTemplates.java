package com.enonic.wem.api.content.page;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public class PageTemplates
    extends AbstractImmutableEntityList<PageTemplate>
{
    private PageTemplates( final ImmutableList<PageTemplate> list )
    {
        super( list );
    }

    public static PageTemplates empty()
    {
        final ImmutableList<PageTemplate> list = ImmutableList.of();
        return new PageTemplates( list );
    }

    public static PageTemplates from( final PageTemplate... templates )
    {
        return new PageTemplates( ImmutableList.copyOf( templates ) );
    }

    public static PageTemplates from( final Iterable<? extends PageTemplate> templates )
    {
        return new PageTemplates( ImmutableList.copyOf( templates ) );
    }

    public static PageTemplates from( final Collection<? extends PageTemplate> templates )
    {
        return new PageTemplates( ImmutableList.copyOf( templates ) );
    }

}
