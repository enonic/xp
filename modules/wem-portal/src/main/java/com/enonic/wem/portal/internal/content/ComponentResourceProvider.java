package com.enonic.wem.portal.internal.content;

import com.enonic.wem.portal.internal.rendering.RendererFactory;

public final class ComponentResourceProvider
    extends RenderBaseResourceProvider<ComponentResource2>
{
    private RendererFactory rendererFactory;

    @Override
    public Class<ComponentResource2> getType()
    {
        return ComponentResource2.class;
    }

    @Override
    public ComponentResource2 newResource()
    {
        final ComponentResource2 instance = new ComponentResource2();
        instance.rendererFactory = this.rendererFactory;
        configure( instance );
        return instance;
    }

    public void setRendererFactory( final RendererFactory rendererFactory )
    {
        this.rendererFactory = rendererFactory;
    }
}
