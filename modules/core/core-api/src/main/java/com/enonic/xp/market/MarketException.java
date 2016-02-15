package com.enonic.xp.market;

public class MarketException
    extends RuntimeException
{
    private Integer httpErrorCode = 500;

    public MarketException( final String message )
    {
        super( message );
    }

    public MarketException( final String message, int httpErrorCode )
    {
        super( message + " [code = " + httpErrorCode + "]" );
        this.httpErrorCode = httpErrorCode;
    }

    public MarketException( final String message, final Throwable cause )
    {
        super( message, cause );
    }

    public int getHttpErrorCode()
    {
        return httpErrorCode;
    }
}
