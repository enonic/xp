package com.enonic.wem.api.exception;

import com.enonic.wem.api.NotFoundException;
import com.enonic.wem.api.schema.content.ContentTypeName;

public final class ContentTypeNotFoundException
    extends NotFoundException
{
    public ContentTypeNotFoundException( final ContentTypeName contentTypeName )
    {
        super( "ContentType [{0}] was not found", contentTypeName );
    }
}
