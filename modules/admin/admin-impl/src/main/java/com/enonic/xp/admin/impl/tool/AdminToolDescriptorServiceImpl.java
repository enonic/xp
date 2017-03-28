package com.enonic.xp.admin.impl.tool;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.admin.tool.AdminToolDescriptors;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.PrincipalKeys;

@Component(immediate = true)
public final class AdminToolDescriptorServiceImpl
    implements AdminToolDescriptorService
{
    private ApplicationService applicationService;

    private ResourceService resourceService;

    @Override
    public AdminToolDescriptors getAllowedAdminToolDescriptors( final PrincipalKeys principalKeys )
    {
        return new GetAdminToolDescriptorsCommand().
            applicationService( this.applicationService ).
            resourceService( this.resourceService ).
            filter( adminToolDescriptor -> adminToolDescriptor.isAccessAllowed( principalKeys ) ).
            execute();
    }

    @Override
    public AdminToolDescriptor getByKey( final DescriptorKey descriptorKey )
    {
        return new GetAdminToolDescriptorCommand().
            resourceService( this.resourceService ).
            descriptorKey( descriptorKey ).
            execute();
    }


    @Override
    public String getIconByKey( final DescriptorKey descriptorKey )
    {
        return new GetAdminToolIconCommand().
            resourceService( this.resourceService ).
            descriptorKey( descriptorKey ).
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
