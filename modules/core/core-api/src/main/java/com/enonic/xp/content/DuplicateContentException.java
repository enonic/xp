package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class DuplicateContentException
    extends RuntimeException
{

    public DuplicateContentException( final String message )
    {
        super( message );
    }
}
