package com.enonic.wem.portal.postprocess;


public class RendererNotFoundException
    extends RuntimeException
{
    public RendererNotFoundException( final Object subject )
    {
        super( "No Renderer found for: " + subject.getClass().getSimpleName() );
    }
}
