package com.enonic.wem.portal.postprocess;


public class PostProcessException
    extends RuntimeException
{
    public PostProcessException( final String message, final Exception e )
    {
        super( message, e );
    }
}
