package com.enonic.wem.api.schema.content.validator;

import com.enonic.wem.api.exception.BaseException;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;

public class InvalidContentTypeException
    extends BaseException
{

    private final String validationMessage;

    public InvalidContentTypeException( final ContentType contentType )
    {
        super( buildMessage( contentType ) );
        this.validationMessage = super.getMessage();
    }

    public InvalidContentTypeException( final ContentTypeName contentTypeName, final String validationMessage )
    {
        super( buildMessage( contentTypeName, validationMessage ) );
        this.validationMessage = validationMessage;
    }

    public String getValidationMessage()
    {
        return validationMessage;
    }

    private static String buildMessage( final ContentType contentType )
    {
        final StringBuilder s = new StringBuilder();
        s.append( "Invalid ContentType: " ).append( contentType );
        return s.toString();
    }

    private static String buildMessage( final ContentTypeName contentTypeName, final String message )
    {
        final StringBuilder s = new StringBuilder();
        s.append( "Invalid ContentType: [" ).append( contentTypeName ).append( "]: " ).append( message );
        return s.toString();
    }
}
