package com.enonic.xp.admin.impl.tool;

import java.util.Set;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.xml.XmlException;

abstract class AbstractGetAdminToolDescriptorCommand<T extends AbstractGetAdminToolDescriptorCommand>
{
    private final static String PATH = "/admin/tools";

    protected ResourceService resourceService;

    public T resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        return (T) this;
    }

    protected Set<DescriptorKey> findDescriptorKeys( final ApplicationKey key )
    {
        return new DescriptorKeyLocator( this.resourceService, PATH, false ).findKeys( key );
    }

    protected ResourceProcessor<DescriptorKey, AdminToolDescriptor> createProcessor( final DescriptorKey key )
    {
        return new ResourceProcessor.Builder<DescriptorKey, AdminToolDescriptor>().
            key( key ).
            segment( "adminToolDescriptor" ).
            keyTranslator( AdminToolDescriptor::toResourceKey ).
            processor( resource -> loadDescriptor( key, resource ) ).
            build();
    }

    protected AdminToolDescriptor loadDescriptor( final DescriptorKey key, final Resource resource )
    {
        final AdminToolDescriptor.Builder builder = AdminToolDescriptor.create().
            key( key );
        parseXml( resource, builder );
        return builder.build();
    }

    protected void parseXml( final Resource resource, final AdminToolDescriptor.Builder builder )
    {
        try
        {
            final XmlAdminToolDescriptorParser parser = new XmlAdminToolDescriptorParser();
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
