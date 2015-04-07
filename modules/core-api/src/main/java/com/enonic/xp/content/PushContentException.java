package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class PushContentException
    extends RuntimeException
{

    public PushContentException( final String message )
    {
        super( message );
    }
}

