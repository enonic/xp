package com.enonic.xp.layer;

import com.google.common.annotations.Beta;

@Beta
public class ContentLayerException
    extends RuntimeException
{
    public ContentLayerException( final String message )
    {
        super( message );
    }
}
