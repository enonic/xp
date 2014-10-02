package com.enonic.wem.portal.internal.content;

import com.enonic.wem.portal.internal.rendering.RendererFactory;

public final class ComponentResourceProvider
    extends RenderBaseResourceProvider<ComponentResource>
{
    private RendererFactory rendererFactory;

    @Override
    public Class<ComponentResource> getType()
    {
        return ComponentResource.class;
    }

    @Override
    public ComponentResource newResource()
    {
        final ComponentResource instance = new ComponentResource();
        instance.rendererFactory = this.rendererFactory;
        configure( instance );
        return instance;
    }

    public void setRendererFactory( final RendererFactory rendererFactory )
    {
        this.rendererFactory = rendererFactory;
    }
}
