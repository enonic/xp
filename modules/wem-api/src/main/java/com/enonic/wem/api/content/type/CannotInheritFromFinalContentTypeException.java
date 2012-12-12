package com.enonic.wem.api.content.type;

public class CannotInheritFromFinalContentTypeException
    extends InvalidContentTypeException
{
    public CannotInheritFromFinalContentTypeException( final ContentType contentType )
    {
        super( contentType );
    }

    public CannotInheritFromFinalContentTypeException( final ContentType contentType, final String message )
    {
        super( contentType, message );
    }
}
