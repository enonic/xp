package com.enonic.wem.api.rendering;


import com.enonic.wem.api.Client;

public class Renderer
{
    private Client client = null;

    RenderingResult renderComponent( Component component )
    {

        final ComponentType componentType = resolveComponentType( component );
        componentType.execute( component, new Context(), client );

        return new RenderingResult();
    }

    private ComponentType resolveComponentType( final Component component )
    {
        return null;
    }
}
