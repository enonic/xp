package com.enonic.wem.api.schema.content;


public class DeleteContentTypeResult
{
    public final ContentType deletedContentType;

    public DeleteContentTypeResult( final ContentType deletedContentType )
    {
        this.deletedContentType = deletedContentType;
    }
}
