package com.enonic.wem.portal.internal.content;

public final class ComponentResourceProvider
    extends RenderBaseResourceProvider<ComponentResource>
{
    @Override
    public Class<ComponentResource> getType()
    {
        return ComponentResource.class;
    }

    @Override
    public ComponentResource newResource()
    {
        final ComponentResource instance = new ComponentResource();
        configure( instance );
        return instance;
    }
}
