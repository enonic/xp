package com.enonic.xp.portal.impl.rendering;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.region.Descriptor;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptorService;

@Component(immediate = true, service = Renderer.class)
public final class LayoutRenderer
    extends DescriptorBasedComponentRenderer<LayoutComponent>
{
    protected LayoutDescriptorService layoutDescriptorService;

    private PostProcessor postProcessor;

    @Override
    public Class<LayoutComponent> getType()
    {
        return LayoutComponent.class;
    }

    @Override
    protected Descriptor getComponentDescriptor( final DescriptorKey descriptorKey )
    {
        return layoutDescriptorService.getByKey( descriptorKey );
    }

    @Override
    public final PortalResponse render( final LayoutComponent component, final PortalRequest portalRequest ) {
        final PortalResponse portalResponse = super.render( component, portalRequest );
        return this.postProcessor.processResponseInstructions( portalRequest, portalResponse );
    }


    @Reference
    public void setPostProcessor( final PostProcessor postProcessor )
    {
        this.postProcessor = postProcessor;
    }

    @Reference
    public void setLayoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
    }

    @Override
    @Reference
    public void setControllerScriptFactory( final ControllerScriptFactory value )
    {
        super.setControllerScriptFactory( value );
    }
}
