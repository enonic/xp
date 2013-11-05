package com.enonic.wem.api.exception;

import com.enonic.wem.api.schema.content.ContentTypeName;

public final class UnableToDeleteContentTypeException
    extends BaseException
{
    public UnableToDeleteContentTypeException( final ContentTypeName contentTypeName, final String reason )
    {
        super( "Unable to delete content type [{0}]: " + reason, contentTypeName );
    }
}
