package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class UnpublishContentException
    extends RuntimeException
{

    public UnpublishContentException( final String message )
    {
        super( message );
    }
}
