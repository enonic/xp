package com.enonic.xp.core.impl.project.layer;

import java.text.MessageFormat;

import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.project.layer.ContentLayerKey;

public final class LayerNotFoundException
    extends NotFoundException
{
    public LayerNotFoundException( final ContentLayerKey contentLayerKey )
    {
        super( MessageFormat.format( "Layer [{0}] was not found", contentLayerKey.toString() ) );
    }
}
