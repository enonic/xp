package com.enonic.wem.api.content.page;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public class LayoutTemplates
    extends AbstractImmutableEntityList<LayoutTemplate>
{
    private LayoutTemplates( final ImmutableList<LayoutTemplate> list )
    {
        super( list );
    }

    public static LayoutTemplates empty()
    {
        final ImmutableList<LayoutTemplate> list = ImmutableList.of();
        return new LayoutTemplates( list );
    }

    public static LayoutTemplates from( final LayoutTemplate... templates )
    {
        return new LayoutTemplates( ImmutableList.copyOf( templates ) );
    }

    public static LayoutTemplates from( final Iterable<? extends LayoutTemplate> templates )
    {
        return new LayoutTemplates( ImmutableList.copyOf( templates ) );
    }

    public static LayoutTemplates from( final Collection<? extends LayoutTemplate> templates )
    {
        return new LayoutTemplates( ImmutableList.copyOf( templates ) );
    }

}
