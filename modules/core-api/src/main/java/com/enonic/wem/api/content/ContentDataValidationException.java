package com.enonic.wem.api.content;


import com.enonic.wem.api.exception.BaseException;

public class ContentDataValidationException
    extends BaseException
{
    public ContentDataValidationException( final String message )
    {
        super( message );
    }
}
