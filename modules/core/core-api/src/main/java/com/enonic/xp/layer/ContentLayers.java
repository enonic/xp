package com.enonic.xp.layer;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class ContentLayers
    extends AbstractImmutableEntitySet<ContentLayer>
{
    private ContentLayers( final ImmutableSet<ContentLayer> set )
    {
        super( set );
    }

    public static ContentLayers from( final ContentLayer... contentLayers )
    {
        return new ContentLayers( ImmutableSet.copyOf( contentLayers ) );
    }

    public static ContentLayers from( final Iterable<ContentLayer> contentLayers )
    {
        return new ContentLayers( ImmutableSet.copyOf( contentLayers ) );
    }

    public static ContentLayers empty()
    {
        return new ContentLayers( ImmutableSet.of() );
    }
}
