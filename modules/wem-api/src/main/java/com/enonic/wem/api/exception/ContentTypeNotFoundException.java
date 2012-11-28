package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.type.QualifiedContentTypeName;

public final class ContentTypeNotFoundException
    extends BaseException
{
    public ContentTypeNotFoundException( final QualifiedContentTypeName qualifiedContentTypeName )
    {
        super( "Content type [{0}] was not found", qualifiedContentTypeName );
    }
}
