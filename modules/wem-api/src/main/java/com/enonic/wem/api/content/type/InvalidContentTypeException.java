package com.enonic.wem.api.content.type;

public class InvalidContentTypeException
    extends RuntimeException
{

    private ContentType contentType;

    public InvalidContentTypeException( final ContentType contentType )
    {
        super( buildMessage( contentType ) );
        this.contentType = contentType;
    }

    public InvalidContentTypeException( final ContentType contentType, final String message )
    {
        super( buildMessage( contentType, message ) );
        this.contentType = contentType;
    }

    public ContentType getContentType()
    {
        return contentType;
    }

    private static String buildMessage( final ContentType contentType )
    {
        final StringBuilder s = new StringBuilder();
        s.append( "Invalid content type: " ).append( contentType );
        return s.toString();
    }

    private static String buildMessage( final ContentType contentType, final String message )
    {
        final StringBuilder s = new StringBuilder();
        s.append( "Invalid content type: [" ).append( contentType ).append( "]: " ).append( message );
        return s.toString();
    }
}
