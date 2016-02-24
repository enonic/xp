package com.enonic.xp.admin.impl.widget;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.admin.widget.WidgetDescriptors;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceService;

@Component(immediate = true)
public final class WidgetDescriptorServiceImpl
    implements WidgetDescriptorService
{
    private ApplicationService applicationService;

    private ResourceService resourceService;

    @Override
    public WidgetDescriptors getByInterfaces( final String... interfaceNames )
    {
        return new GetWidgetDescriptorsByInterfaceCommand().
            applicationService( this.applicationService ).
            resourceService( this.resourceService ).
            interfaceNames( interfaceNames ).
            execute();
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
