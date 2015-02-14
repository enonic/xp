package com.enonic.xp.core.schema.content;

import com.google.common.base.Preconditions;

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
