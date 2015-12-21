package com.enonic.xp.admin.impl.adminapp;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.enonic.xp.admin.adminapp.AdminApplicationDescriptor;
import com.enonic.xp.admin.adminapp.AdminApplicationDescriptors;
import com.enonic.xp.app.ApplicationService;

public final class GetAdminApplicationDescriptorsCommand
    extends AbstractGetAdminApplicationDescriptorCommand<GetAdminApplicationDescriptorsCommand>
{
    private ApplicationService applicationService;

    private Predicate<AdminApplicationDescriptor> filter;

    public GetAdminApplicationDescriptorsCommand applicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
        return this;
    }

    public GetAdminApplicationDescriptorsCommand filter( final Predicate<AdminApplicationDescriptor> filter )
    {
        this.filter = filter;
        return this;
    }

    public AdminApplicationDescriptors execute()
    {
        final List<AdminApplicationDescriptor> applicationDescriptors = applicationService.getAllApplications().
            stream().
            flatMap( application -> findDescriptorKeys( application.getKey() ).stream() ).
            map( this::createProcessor ).
            map( processor -> this.resourceService.processResource( processor ) ).
            filter( adminApplicationDescriptor -> filter == null || filter.test( adminApplicationDescriptor ) ).
            collect( Collectors.toList() );

        return AdminApplicationDescriptors.from( applicationDescriptors );
    }
}
