package com.enonic.wem.core.rendering;


import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.image.Image;
import com.enonic.wem.api.content.page.Component;
import com.enonic.wem.api.content.page.Layout;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.Part;

public final class Renderer
{
    private final ConcurrentMap<Class<? extends Component>, ComponentExecutor> componentRegister;

    private final Client client;

    public Renderer( final Client client )
    {
        this.client = client;
        this.componentRegister = Maps.newConcurrentMap();
        registerComponentType( Page.class, new PageComponentType( client ) );
        registerComponentType( Part.class, new PartComponentType() );
        registerComponentType( Layout.class, new LayoutComponentType() );
        registerComponentType( Image.class, new ImageComponentType() );
    }

    public RenderingResult renderComponent( final Component component )
    {
        final ComponentExecutor componentType = resolveComponentType( component );
        final Context context = new Context();
        final RenderingResult result = componentType.execute( component, context );

        return result;
    }

    private ComponentExecutor resolveComponentType( final Component component )
    {
        return this.componentRegister.get( component.getClass() );
    }

    public void registerComponentType( final Class<? extends Component> component, final ComponentExecutor componentType )
    {
        this.componentRegister.put( component, componentType );
    }
}
