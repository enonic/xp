package com.enonic.xp.schema.content.validator;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.content.ContentTypeName;

@PublicApi
public final class ContentTypeValidationError
{
    private final String validationMessage;

    public ContentTypeValidationError( final String validationMessage, final ContentTypeName contentTypeName )
    {
        this.validationMessage = buildMessage( contentTypeName, validationMessage );
    }

    public String getErrorMessage()
    {
        return validationMessage;
    }

    private static String buildMessage( final ContentTypeName contentTypeName, final String message )
    {
        return "Invalid content type: [" + contentTypeName + "]: " + message;
    }
}
