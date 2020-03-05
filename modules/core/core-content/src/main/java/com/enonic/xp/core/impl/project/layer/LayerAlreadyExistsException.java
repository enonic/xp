package com.enonic.xp.core.impl.project.layer;

import java.text.MessageFormat;

import com.enonic.xp.exception.BaseException;
import com.enonic.xp.project.layer.ContentLayerKey;

public final class LayerAlreadyExistsException
    extends BaseException
{
    private final ContentLayerKey key;

    public LayerAlreadyExistsException( final ContentLayerKey key )
    {
        super( MessageFormat.format( "Layer with key [{0}] already exists", key ) );
        this.key = key;
    }

    public ContentLayerKey getKey()
    {
        return key;
    }
}
