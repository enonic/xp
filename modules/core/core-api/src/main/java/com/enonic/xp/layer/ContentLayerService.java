package com.enonic.xp.layer;

public interface ContentLayerService
{
    ContentLayers list();

    ContentLayer get( ContentLayerName name );

    ContentLayer create( final CreateContentLayerParams params );
}
