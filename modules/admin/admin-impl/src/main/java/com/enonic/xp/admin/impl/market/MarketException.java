package com.enonic.xp.admin.impl.market;

class MarketException
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
