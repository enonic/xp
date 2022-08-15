package com.enonic.xp.portal.impl.rendering;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptorService;

@Component(immediate = true, service = Renderer.class)
public final class LayoutRenderer
    extends DescriptorBasedComponentRenderer<LayoutComponent>
{
    private final LayoutDescriptorService layoutDescriptorService;

    @Activate
    public LayoutRenderer( @Reference final ControllerScriptFactory controllerScriptFactory,
                           @Reference final LayoutDescriptorService layoutDescriptorService )
    {
        super( controllerScriptFactory );
        this.layoutDescriptorService = layoutDescriptorService;
    }

    @Override
    public Class<LayoutComponent> getType()
    {
        return LayoutComponent.class;
    }

    @Override
    protected ComponentDescriptor getComponentDescriptor( final DescriptorKey descriptorKey )
    {
        return layoutDescriptorService.getByKey( descriptorKey );
    }
}
