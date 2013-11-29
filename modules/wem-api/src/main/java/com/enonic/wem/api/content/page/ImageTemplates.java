package com.enonic.wem.api.content.page;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public class ImageTemplates
    extends AbstractImmutableEntityList<ImageTemplate>
{
    private ImageTemplates( final ImmutableList<ImageTemplate> list )
    {
        super( list );
    }

    public static ImageTemplates empty()
    {
        final ImmutableList<ImageTemplate> list = ImmutableList.of();
        return new ImageTemplates( list );
    }

    public static ImageTemplates from( final ImageTemplate... templates )
    {
        return new ImageTemplates( ImmutableList.copyOf( templates ) );
    }

    public static ImageTemplates from( final Iterable<? extends ImageTemplate> templates )
    {
        return new ImageTemplates( ImmutableList.copyOf( templates ) );
    }

    public static ImageTemplates from( final Collection<? extends ImageTemplate> templates )
    {
        return new ImageTemplates( ImmutableList.copyOf( templates ) );
    }

}
