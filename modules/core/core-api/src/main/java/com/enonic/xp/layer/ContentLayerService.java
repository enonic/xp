package com.enonic.xp.layer;

import com.google.common.io.ByteSource;

public interface ContentLayerService
{
    ContentLayers list();

    ContentLayer get( final ContentLayerName name );

    GetContentLayerIconResult getIcon( final ContentLayerName name );

    ContentLayer create( final CreateContentLayerParams params );

    ContentLayer update( final UpdateContentLayerParams params );

    ContentLayer delete( final ContentLayerName name );
}
