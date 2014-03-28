package com.enonic.wem.api.command.schema.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.schema.content.ContentType;

public class ValidateContentTypeParams
{
    private ContentType contentType;

    public ValidateContentTypeParams()
    {
    }

    public ValidateContentTypeParams contentType( final ContentType contentType )
    {
        this.contentType = contentType;
        return this;
    }

    public ContentType getContentType()
    {
        return contentType;
    }

    public void validate()
    {
        Preconditions.checkNotNull( contentType, "Content type cannot be null" );
    }
}
