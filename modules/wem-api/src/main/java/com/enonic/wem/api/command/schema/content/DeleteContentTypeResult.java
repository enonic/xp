package com.enonic.wem.api.command.schema.content;


import com.enonic.wem.api.schema.content.ContentType;

public class DeleteContentTypeResult
{
    public final ContentType deletedContentType;

    public DeleteContentTypeResult( final ContentType deletedContentType )
    {
        this.deletedContentType = deletedContentType;
    }
}
