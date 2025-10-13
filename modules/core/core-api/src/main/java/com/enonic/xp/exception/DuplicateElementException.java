package com.enonic.xp.exception;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class DuplicateElementException
    extends BaseException
{
    public DuplicateElementException( final String message )
    {
        super( message );
    }
}
