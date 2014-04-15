package com.enonic.wem.api.schema.content;

import java.text.MessageFormat;

import com.google.common.base.Joiner;

import com.enonic.wem.api.exception.NotFoundException;

public final class ContentTypeNotFoundException
    extends NotFoundException
{
    public ContentTypeNotFoundException( final ContentTypeName contentTypeName )
    {
        super( "ContentType [{0}] was not found", contentTypeName );
    }

    public ContentTypeNotFoundException( final ContentTypeNames contentTypeNames )
    {
        super( MessageFormat.format( "ContentTypes [{0}] were not found", Joiner.on( ", " ).join( contentTypeNames ) ) );
    }
}
