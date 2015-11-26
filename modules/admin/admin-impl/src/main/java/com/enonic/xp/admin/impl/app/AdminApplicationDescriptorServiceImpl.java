package com.enonic.xp.admin.impl.app;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.app.AdminApplicationDescriptorService;
import com.enonic.xp.admin.app.AdminApplicationDescriptors;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceService;

@Component(immediate = true)
public final class AdminApplicationDescriptorServiceImpl
    implements AdminApplicationDescriptorService
{
    private ApplicationService applicationService;

    private ResourceService resourceService;

    @Override
    public AdminApplicationDescriptors getAll()
    {
        return GetAllAdminApplicationDescriptorCommand.
            create().
            applicationService( this.applicationService ).
            resourceService( this.resourceService ).
            build().
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
