package com.enonic.wem.api.exception;

import com.enonic.wem.api.schema.content.QualifiedContentTypeName;

public final class ContentTypeNotFoundException
    extends BaseException
{
    public ContentTypeNotFoundException( final QualifiedContentTypeName qualifiedContentTypeName )
    {
        super( "ContentType [{0}] was not found", qualifiedContentTypeName );
    }
}
