package com.enonic.wem.api.rendering;


import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.image.Image;
import com.enonic.wem.api.content.image.ImageComponentType;
import com.enonic.wem.api.content.page.Layout;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.Part;
import com.enonic.wem.api.content.page.rendering.LayoutComponentType;
import com.enonic.wem.api.content.page.rendering.PageComponentType;
import com.enonic.wem.api.content.page.rendering.PartComponentType;

public final class Renderer
{
    private final ConcurrentMap<Class<? extends Component>, ComponentType> componentRegister;

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
        final ComponentType componentType = resolveComponentType( component );
        final Context context = new Context();
        final RenderingResult result = componentType.execute( component, context );

        return result;
    }

    private ComponentType resolveComponentType( final Component component )
    {
        return this.componentRegister.get( component.getClass() );
    }

    public void registerComponentType( final Class<? extends Component> component, final ComponentType componentType )
    {
        this.componentRegister.put( component, componentType );
    }
}
