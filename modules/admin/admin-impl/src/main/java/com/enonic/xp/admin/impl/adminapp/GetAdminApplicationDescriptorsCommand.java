package com.enonic.xp.admin.impl.adminapp;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.enonic.xp.admin.adminapp.AdminApplicationDescriptor;
import com.enonic.xp.admin.adminapp.AdminApplicationDescriptors;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.xml.XmlException;

final class GetAdminApplicationDescriptorsCommand
{
    private final static String PATH = "/admin/apps";

    private ApplicationService applicationService;

    private ResourceService resourceService;

    private Predicate<AdminApplicationDescriptor> filter;

    public GetAdminApplicationDescriptorsCommand applicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
        return this;
    }

    public GetAdminApplicationDescriptorsCommand resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
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

    private List<DescriptorKey> findDescriptorKeys( final ApplicationKey key )
    {
        return new DescriptorKeyLocator2( this.resourceService, PATH, false ).findKeys( key );
    }

    private ResourceProcessor<DescriptorKey, AdminApplicationDescriptor> createProcessor( final DescriptorKey key )
    {
        return new ResourceProcessor.Builder<DescriptorKey, AdminApplicationDescriptor>().
            key( key ).
            segment( "adminAppDescriptor" ).
            keyTranslator( AdminApplicationDescriptor::toResourceKey ).
            processor( resource -> loadDescriptor( key, resource ) ).
            build();
    }

    private AdminApplicationDescriptor loadDescriptor( final DescriptorKey key, final Resource resource )
    {
        final AdminApplicationDescriptor.Builder builder = AdminApplicationDescriptor.create().
            key( key );
        parseXml( resource, builder );
        return builder.build();
    }

    private void parseXml( final Resource resource, final AdminApplicationDescriptor.Builder builder )
    {
        try
        {
            final XmlAdminApplicationDescriptorParser parser = new XmlAdminApplicationDescriptorParser();
            parser.builder( builder );
            parser.source( resource.readString() );
            parser.parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load admin app descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
        }
    }
}
