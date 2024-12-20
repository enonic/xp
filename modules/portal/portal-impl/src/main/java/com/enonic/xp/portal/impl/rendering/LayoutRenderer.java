package com.enonic.xp.portal.impl.rendering;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptorService;

@Component(immediate = true, service = Renderer.class)
public final class LayoutRenderer
    extends DescriptorBasedComponentRenderer<LayoutComponent>
{
    @Activate
    public LayoutRenderer( @Reference final ControllerScriptFactory controllerScriptFactory,
                           @Reference final LayoutDescriptorService layoutDescriptorService )
    {
        super( controllerScriptFactory, LayoutComponent.class, layoutDescriptorService::getByKey );
    }
}
