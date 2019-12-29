package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class PushContentException
    extends RuntimeException
{

    public PushContentException( final String message )
    {
        super( message );
    }
}

