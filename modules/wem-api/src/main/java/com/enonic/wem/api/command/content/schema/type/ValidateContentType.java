package com.enonic.wem.api.command.content.schema.type;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.schema.type.ContentType;
import com.enonic.wem.api.content.schema.type.validator.ContentTypeValidationResult;

public class ValidateContentType
    extends Command<ContentTypeValidationResult>
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
