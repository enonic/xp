package com.enonic.wem.portal.internal.content;

import com.enonic.wem.portal.internal.rendering.RendererFactory;

public final class ComponentResourceFactory
    extends RenderBaseResourceFactory<ComponentResource>
{
    private RendererFactory rendererFactory;

    public ComponentResourceFactory()
    {
        super( ComponentResource.class );
    }

    @Override
    protected void configure( final ComponentResource instance )
    {
        super.configure( instance );
        instance.rendererFactory = this.rendererFactory;
    }

    public void setRendererFactory( final RendererFactory rendererFactory )
    {
        this.rendererFactory = rendererFactory;
    }
}
