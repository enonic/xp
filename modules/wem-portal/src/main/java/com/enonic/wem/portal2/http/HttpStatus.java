package com.enonic.wem.portal2.http;

import com.google.common.collect.Range;

public enum HttpStatus
{
    // Informational
    CONTINUE( 100, "Continue" ),

    // Successful
    OK( 200, "OK" ),
    CREATED( 201, "Created" ),
    ACCEPTED( 202, "Accepted" ),
    NO_CONTENT( 204, "No Content" ),

    // Redirection
    MOVED_PERMANENTLY( 301, "Moved Permanently" ),
    FOUND( 302, "Found" ),
    SEE_OTHER( 303, "See Other" ),
    NOT_MODIFIED( 304, "Not Modified" ),
    USE_PROXY( 305, "Use Proxy" ),
    TEMPORARY_REDIRECT( 307, "Temporary Redirect" ),

    // Client error
    BAD_REQUEST( 400, "Bad Request" ),
    UNAUTHORIZED( 401, "Unauthorized" ),
    PAYMENT_REQUIRED( 402, "Payment Required" ),
    FORBIDDEN( 403, "Forbidden" ),
    NOT_FOUND( 404, "Not Found" ),
    METHOD_NOT_ALLOWED( 405, "Method Not Allowed" ),
    NOT_ACCEPTABLE( 406, "Not Acceptable" ),
    PROXY_AUTHENTICATION_REQUIRED( 407, "Proxy Authentication Required" ),
    REQUEST_TIMEOUT( 408, "Request Timeout" ),
    CONFLICT( 409, "Conflict" ),
    GONE( 410, "Gone" ),
    LENGTH_REQUIRED( 411, "Length Required" ),
    PRECONDITION_FAILED( 412, "Precondition Failed" ),
    UNSUPPORTED_MEDIA_TYPE( 415, "Unsupported Media Type" ),
    TOO_MANY_REQUESTS( 429, "Too Many Requests" ),

    // Server error
    INTERNAL_SERVER_ERROR( 500, "Internal Server Error" ),
    NOT_IMPLEMENTED( 501, "Not Implemented" ),
    BAD_GATEWAY( 502, "Bad Gateway" ),
    SERVICE_UNAVAILABLE( 503, "Service Unavailable" ),
    GATEWAY_TIMEOUT( 504, "Gateway Timeout" );

    private final int code;

    private final String reasonPhrase;

    private HttpStatus( final int code, final String reasonPhrase )
    {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public int getCode()
    {
        return this.code;
    }

    public String getReasonPhrase()
    {
        return this.reasonPhrase;
    }

    public boolean isInformational()
    {
        return Range.closed( 100, 199 ).contains( this.code );
    }

    public boolean isSuccessful()
    {
        return Range.closed( 200, 299 ).contains( this.code );
    }

    public boolean isRedirection()
    {
        return Range.closed( 300, 399 ).contains( this.code );
    }

    public boolean isClientError()
    {
        return Range.closed( 400, 499 ).contains( this.code );
    }

    public boolean isServerError()
    {
        return Range.closed( 500, 599 ).contains( this.code );
    }

    @Override
    public String toString()
    {
        return Integer.toString( this.code );
    }

    public static HttpStatus valueOf( final int code )
    {
        for ( final HttpStatus status : values() )
        {
            if ( status.code == code )
            {
                return status;
            }
        }

        throw new IllegalArgumentException( "No matching constant for [" + code + "]" );
    }
}
