package com.enonic.wem.api.schema.content;

import com.enonic.wem.api.exception.BaseException;

public final class ContentTypeAlreadyExistException
    extends BaseException
{
    public ContentTypeAlreadyExistException( final ContentTypeName name )
    {
        super( "ContentType [{0}] already exists", name );
    }
}
