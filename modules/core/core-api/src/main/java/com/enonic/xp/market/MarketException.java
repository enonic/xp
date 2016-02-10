package com.enonic.xp.market;

public class MarketException
    extends RuntimeException
{
    public MarketException( final String message )
    {
        super( message );
    }

    public MarketException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
