package com.enonic.xp.portal.impl.rendering;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartDescriptorService;

@Component(immediate = true, service = Renderer.class)
public final class PartRenderer
    extends DescriptorBasedComponentRenderer<PartComponent>
{
    private final PartDescriptorService partDescriptorService;

    @Activate
    public PartRenderer( @Reference final ControllerScriptFactory controllerScriptFactory, @Reference final PartDescriptorService partDescriptorService )
    {
        super( controllerScriptFactory );
        this.partDescriptorService = partDescriptorService;
    }

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
}
