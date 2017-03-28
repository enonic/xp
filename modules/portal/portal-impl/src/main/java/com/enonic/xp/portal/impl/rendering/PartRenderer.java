package com.enonic.xp.portal.impl.rendering;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartDescriptorService;

@Component(immediate = true, service = Renderer.class)
public final class PartRenderer
    extends DescriptorBasedComponentRenderer<PartComponent>
{
    private PartDescriptorService partDescriptorService;

    @Override
    public Class<PartComponent> getType()
    {
        return PartComponent.class;
    }

    @Override
    protected ComponentDescriptor getComponentDescriptor( final DescriptorKey descriptorKey )
    {
        return this.partDescriptorService.getByKey( descriptorKey );
    }

    @Reference
    public void setPartDescriptorService( final PartDescriptorService value )
    {
        this.partDescriptorService = value;
    }

    @Override
    @Reference
    public void setControllerScriptFactory( final ControllerScriptFactory value )
    {
        super.setControllerScriptFactory( value );
    }
}
