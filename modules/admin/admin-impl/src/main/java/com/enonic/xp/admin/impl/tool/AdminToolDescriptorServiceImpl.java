package com.enonic.xp.admin.impl.tool;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.admin.tool.AdminToolDescriptors;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.xml.XmlException;

@Component(immediate = true)
public final class AdminToolDescriptorServiceImpl
    implements AdminToolDescriptorService
{
    private static final String PATH = "/admin/tools";

    private final ApplicationService applicationService;

    private final ResourceService resourceService;

    private final DescriptorKeyLocator descriptorKeyLocator;

    @Activate
    public AdminToolDescriptorServiceImpl( @Reference final ResourceService resourceService,
                                           @Reference final ApplicationService applicationService )
    {
        this.resourceService = resourceService;
        this.descriptorKeyLocator = new DescriptorKeyLocator( resourceService, PATH, false );
        this.applicationService = applicationService;
    }

    @Override
    public AdminToolDescriptors getAllowedAdminToolDescriptors( final PrincipalKeys principalKeys )
    {
        return applicationService.getInstalledApplications()
            .stream()
            .flatMap( application -> descriptorKeyLocator.findKeys( application.getKey() ).stream() )
            .map( this::getByKey )
            .filter( adminToolDescriptor -> adminToolDescriptor.isAccessAllowed( principalKeys ) )
            .collect( AdminToolDescriptors.collector() );
    }

    @Override
    public AdminToolDescriptors getByApplication( final ApplicationKey applicationKey )
    {
        return descriptorKeyLocator.findKeys( applicationKey ).stream().map( this::getByKey ).collect( AdminToolDescriptors.collector() );
    }

    @Override
    public AdminToolDescriptor getByKey( final DescriptorKey descriptorKey )
    {
        return this.resourceService.processResource( createProcessor( descriptorKey ) );
    }

    @Override
    public String getIconByKey( final DescriptorKey descriptorKey )
    {
        return this.resourceService.processResource( createIconProcessor( descriptorKey ) );
    }

    private ResourceProcessor<DescriptorKey, String> createIconProcessor( final DescriptorKey key )
    {
        return new ResourceProcessor.Builder<DescriptorKey, String>().key( key )
            .segment( "adminToolIcon" )
            .keyTranslator( AdminToolDescriptor::toIconResourceKey )
            .processor( Resource::readString )
            .build();
    }

    private ResourceProcessor<DescriptorKey, AdminToolDescriptor> createProcessor( final DescriptorKey key )
    {
        return new ResourceProcessor.Builder<DescriptorKey, AdminToolDescriptor>().key( key )
            .segment( "adminToolDescriptor" )
            .keyTranslator( AdminToolDescriptor::toResourceKey )
            .processor( resource -> loadDescriptor( key, resource ) )
            .build();
    }

    private AdminToolDescriptor loadDescriptor( final DescriptorKey key, final Resource resource )
    {
        final AdminToolDescriptor.Builder builder = AdminToolDescriptor.create().key( key );
        parseXml( resource, builder );
        return builder.build();
    }

    private void parseXml( final Resource resource, final AdminToolDescriptor.Builder builder )
    {
        try
        {
            final XmlAdminToolDescriptorParser parser = new XmlAdminToolDescriptorParser();
            parser.currentApplication( resource.getKey().getApplicationKey() );
            parser.builder( builder );
            parser.source( resource.readString() );
            parser.parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load admin app descriptor [" + resource.getKey() + "]: " + e.getMessage() );
        }
    }
}
