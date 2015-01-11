package com.enonic.wem.portal.internal.rendering.layout;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.region.Descriptor;
import com.enonic.wem.api.content.page.region.LayoutComponent;
import com.enonic.wem.api.content.page.region.LayoutDescriptorService;
import com.enonic.wem.portal.internal.controller.ControllerScriptFactory;
import com.enonic.wem.portal.internal.rendering.Renderer;
import com.enonic.wem.portal.internal.rendering.base.DescriptorBasedComponentRenderer;

@Component(immediate = true, service = Renderer.class)
public final class LayoutRenderer
    extends DescriptorBasedComponentRenderer<LayoutComponent>
{
    protected LayoutDescriptorService layoutDescriptorService;

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
