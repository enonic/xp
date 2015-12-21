package com.enonic.xp.admin.impl.adminapp;

import java.util.List;

import com.enonic.xp.admin.adminapp.AdminApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.xml.XmlException;

abstract class AbstractGetAdminApplicationDescriptorCommand<T extends AbstractGetAdminApplicationDescriptorCommand>
{
    private final static String PATH = "/admin/apps";

    protected ResourceService resourceService;

    public T resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        return (T) this;
    }

    protected List<DescriptorKey> findDescriptorKeys( final ApplicationKey key )
    {
        return new DescriptorKeyLocator2( this.resourceService, PATH, false ).findKeys( key );
    }

    protected ResourceProcessor<DescriptorKey, AdminApplicationDescriptor> createProcessor( final DescriptorKey key )
    {
        return new ResourceProcessor.Builder<DescriptorKey, AdminApplicationDescriptor>().
            key( key ).
            segment( "adminAppDescriptor" ).
            keyTranslator( AdminApplicationDescriptor::toResourceKey ).
            processor( resource -> loadDescriptor( key, resource ) ).
            build();
    }

    protected AdminApplicationDescriptor loadDescriptor( final DescriptorKey key, final Resource resource )
    {
        final AdminApplicationDescriptor.Builder builder = AdminApplicationDescriptor.create().
            key( key );
        parseXml( resource, builder );
        return builder.build();
    }

    protected void parseXml( final Resource resource, final AdminApplicationDescriptor.Builder builder )
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
