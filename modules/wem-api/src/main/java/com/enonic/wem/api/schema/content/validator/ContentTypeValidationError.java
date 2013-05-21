package com.enonic.wem.api.schema.content.validator;

import com.enonic.wem.api.schema.content.ContentType;

public final class ContentTypeValidationError
{
    private final ContentType contentType;

    private final String validationMessage;

    public ContentTypeValidationError( final String validationMessage, final ContentType contentType )
    {
        this.contentType = contentType;
        this.validationMessage = buildMessage( contentType, validationMessage );
    }

    public ContentType getContentType()
    {
        return contentType;
    }

    public String getErrorMessage()
    {
        return validationMessage;
    }

    private static String buildMessage( final ContentType contentType, final String message )
    {
        final StringBuilder s = new StringBuilder();
        s.append( "Invalid content type: [" ).append( contentType.getQualifiedName() ).append( "]: " ).append( message );
        return s.toString();
    }
}
