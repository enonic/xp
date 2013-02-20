package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.schema.type.QualifiedContentTypeName;

public final class UnableToDeleteContentTypeException
    extends BaseException
{
    public UnableToDeleteContentTypeException( final QualifiedContentTypeName qualifiedContentTypeName, final String reason )
    {
        super( "Unable to delete content type [{0}]: " + reason, qualifiedContentTypeName );
    }
}
