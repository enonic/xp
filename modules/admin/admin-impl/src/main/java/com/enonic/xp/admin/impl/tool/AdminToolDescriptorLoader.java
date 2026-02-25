package com.enonic.xp.admin.impl.tool;

import java.time.Instant;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.DescriptorLoader;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

@Component(immediate = true)
public final class AdminToolDescriptorLoader
    implements DescriptorLoader<AdminToolDescriptor>
{
    private static final String PATH = "/admin/tools";

    private final ResourceService resourceService;

    private final DescriptorKeyLocator descriptorKeyLocator;

    @Activate
    public AdminToolDescriptorLoader( @Reference final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        this.descriptorKeyLocator = new DescriptorKeyLocator( this.resourceService, PATH, false );
    }

    @Override
    public Class<AdminToolDescriptor> getType()
    {
        return AdminToolDescriptor.class;
    }

    @Override
    public DescriptorKeys find( final ApplicationKey key )
    {
        return descriptorKeyLocator.findKeys( key );
    }

    @Override
    public ResourceKey toResource( final DescriptorKey key )
    {
        return toResource( key, "yml" );
    }

    @Override
    public AdminToolDescriptor load( final DescriptorKey key, final Resource resource )
    {
        return YmlAdminToolDescriptorParser.parse( resource.readString(), key.getApplicationKey() )
            .key( key )
            .setIcon( loadIcon( key ) )
            .build();
    }

    @Override
    public AdminToolDescriptor createDefault( final DescriptorKey key )
    {
        return AdminToolDescriptor.create().key( key ).displayName( key.getName() ).build();
    }

    @Override
    public AdminToolDescriptor postProcess( final AdminToolDescriptor descriptor )
    {
        return descriptor;
    }

    private Icon loadIcon( final DescriptorKey key )
    {
        final ResourceKey resourceKey = toResource( key, "svg" );
        final Resource resource = this.resourceService.getResource( resourceKey );

        if ( !resource.exists() )
        {
            return null;
        }

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        return Icon.from( resource.readBytes(), "image/svg+xml", modifiedTime );
    }

    private ResourceKey toResource( final DescriptorKey key, final String extension )
    {
        return ResourceKey.from( key.getApplicationKey(), PATH + "/" + key.getName() + "/" + key.getName() + "." + extension );
    }
}
