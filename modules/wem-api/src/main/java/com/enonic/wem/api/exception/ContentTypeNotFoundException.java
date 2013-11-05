package com.enonic.wem.api.exception;

import com.enonic.wem.api.schema.content.ContentTypeName;

public final class ContentTypeNotFoundException
    extends BaseException
{
    public ContentTypeNotFoundException( final ContentTypeName contentTypeName )
    {
        super( "ContentType [{0}] was not found", contentTypeName );
    }
}
