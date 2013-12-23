package com.enonic.wem.api.schema.content;

import com.enonic.wem.api.exception.BaseException;

public final class UnableToDeleteContentTypeException
    extends BaseException
{
    public UnableToDeleteContentTypeException( final ContentTypeName contentTypeName, final String reason )
    {
        super( "Unable to delete content type [{0}]: " + reason, contentTypeName );
    }
}
