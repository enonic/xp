package com.enonic.wem.api.command.content.type;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ValidateContentTypeResult;

public class ValidateContentType
    extends Command<ValidateContentTypeResult>
{
    private ContentType contentType;

    public ValidateContentType()
    {
    }

    public ValidateContentType contentType( final ContentType contentType )
    {
        this.contentType = contentType;
        return this;
    }

    public ContentType getContentType()
    {
        return contentType;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( contentType, "Content type cannot be null" );
    }
}
