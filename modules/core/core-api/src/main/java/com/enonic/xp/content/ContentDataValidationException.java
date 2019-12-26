package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.BaseException;

@PublicApi
public class ContentDataValidationException
    extends BaseException
{
    public ContentDataValidationException( final String message )
    {
        super( message );
    }
}
