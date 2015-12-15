package com.enonic.xp.admin.impl.adminapp;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.adminapp.AdminApplicationDescriptorService;
import com.enonic.xp.admin.adminapp.AdminApplicationDescriptors;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.PrincipalKeys;

@Component(immediate = true)
public final class AdminApplicationDescriptorServiceImpl
    implements AdminApplicationDescriptorService
{
    private ApplicationService applicationService;

    private ResourceService resourceService;

    @Override
    public AdminApplicationDescriptors getAllowedAdminApplicationDescriptors( final PrincipalKeys principalKeys )
    {
        return new GetAdminApplicationDescriptorsCommand().
            applicationService( this.applicationService ).
            resourceService( this.resourceService ).
            filter( adminApplicationDescriptor -> adminApplicationDescriptor.isAccessAllowed( principalKeys ) ).
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
