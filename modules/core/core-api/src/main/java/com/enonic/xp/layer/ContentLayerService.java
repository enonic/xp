package com.enonic.xp.layer;

import com.google.common.io.ByteSource;

public interface ContentLayerService
{
    ContentLayers list();

    ContentLayer get( ContentLayerName name );

    GetContentLayerIconResult getIcon( ContentLayerName name );

    ContentLayer create( final CreateContentLayerParams params );

    ContentLayer update( final UpdateContentLayerParams params );
}
