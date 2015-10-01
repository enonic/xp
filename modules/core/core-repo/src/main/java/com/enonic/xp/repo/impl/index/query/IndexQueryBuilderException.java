package com.enonic.xp.repo.impl.index.query;

public class IndexQueryBuilderException
    extends RuntimeException
{
    public IndexQueryBuilderException( final String message )
    {
        super( message );
    }

    public IndexQueryBuilderException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
