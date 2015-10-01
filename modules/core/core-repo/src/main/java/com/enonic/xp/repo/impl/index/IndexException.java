package com.enonic.xp.repo.impl.index;

public class IndexException
    extends RuntimeException
{

    public IndexException( final String message )
    {
        super( message );
    }

    public IndexException( final String message, final Exception e )
    {
        super( message, e );
    }

}
