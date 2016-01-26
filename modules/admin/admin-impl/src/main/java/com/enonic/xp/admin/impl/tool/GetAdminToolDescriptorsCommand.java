package com.enonic.xp.admin.impl.tool;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptors;
import com.enonic.xp.app.ApplicationService;

public final class GetAdminToolDescriptorsCommand
    extends AbstractGetAdminToolDescriptorCommand<GetAdminToolDescriptorsCommand>
{
    private ApplicationService applicationService;

    private Predicate<AdminToolDescriptor> filter;

    public GetAdminToolDescriptorsCommand applicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
        return this;
    }

    public GetAdminToolDescriptorsCommand filter( final Predicate<AdminToolDescriptor> filter )
    {
        this.filter = filter;
        return this;
    }

    public AdminToolDescriptors execute()
    {
        final List<AdminToolDescriptor> applicationDescriptors = applicationService.getInstalledApplications().
            stream().
            flatMap( application -> findDescriptorKeys( application.getKey() ).stream() ).
            map( this::createProcessor ).
            map( processor -> this.resourceService.processResource( processor ) ).
            filter( adminToolDescriptor -> filter == null || filter.test( adminToolDescriptor ) ).
            collect( Collectors.toList() );

        return AdminToolDescriptors.from( applicationDescriptors );
    }
}
