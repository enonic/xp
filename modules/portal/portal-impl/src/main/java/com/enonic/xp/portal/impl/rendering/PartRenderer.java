package com.enonic.xp.portal.impl.rendering;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartDescriptorService;

@Component(immediate = true, service = Renderer.class)
public final class PartRenderer
    extends DescriptorBasedComponentRenderer<PartComponent>
{
    @Activate
    public PartRenderer( @Reference final ControllerScriptFactory controllerScriptFactory,
                         @Reference final PartDescriptorService partDescriptorService )
    {
        super( controllerScriptFactory, PartComponent.class, partDescriptorService::getByKey );
    }
}
