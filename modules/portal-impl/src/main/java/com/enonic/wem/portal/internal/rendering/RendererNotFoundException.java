package com.enonic.wem.portal.internal.rendering;

public final class RendererNotFoundException
    extends RuntimeException
{
    RendererNotFoundException( final Class type )
    {
        super( "Renderer for class [" + type.getName() + "] not found." );
    }
}
