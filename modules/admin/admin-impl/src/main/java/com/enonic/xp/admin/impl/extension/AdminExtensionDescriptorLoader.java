package com.enonic.xp.admin.impl.extension;

import java.time.Instant;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
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
public final class AdminExtensionDescriptorLoader
    implements DescriptorLoader<AdminExtensionDescriptor>
{
    private static final String PATH = "/admin/extensions";

    private final ResourceService resourceService;

    private final DescriptorKeyLocator descriptorKeyLocator;

    @Activate
    public AdminExtensionDescriptorLoader( @Reference final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        this.descriptorKeyLocator = new DescriptorKeyLocator( this.resourceService, PATH, false );
    }

    @Override
    public Class<AdminExtensionDescriptor> getType()
    {
        return AdminExtensionDescriptor.class;
    }

    @Override
    public DescriptorKeys find( final ApplicationKey key )
    {
        return descriptorKeyLocator.findKeys( key );
    }

    @Override
    public ResourceKey toResource( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), PATH + "/" + key.getName() + "/" + key.getName() + ".yml" );
    }

    @Override
    public AdminExtensionDescriptor load( final DescriptorKey key, final Resource resource )
    {
        return YmlAdminExtensionDescriptorParser.parse( resource.readString(), key.getApplicationKey() )
            .key( key )
            .setIcon( loadIcon( key ) )
            .build();
    }

    @Override
    public AdminExtensionDescriptor createDefault( final DescriptorKey key )
    {
        return AdminExtensionDescriptor.create().key( key ).displayName( key.getName() ).build();
    }

    @Override
    public AdminExtensionDescriptor postProcess( final AdminExtensionDescriptor descriptor )
    {
        return descriptor;
    }

    private Icon loadIcon( final DescriptorKey key )
    {
        final String iconPath = PATH + "/" + key.getName() + "/" + key.getName() + ".svg";

        final ResourceKey resourceKey = ResourceKey.from( key.getApplicationKey(), iconPath );
        final Resource resource = this.resourceService.getResource( resourceKey );

        if ( !resource.exists() )
        {
            return null;
        }

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        return Icon.from( resource.readBytes(), "image/svg+xml", modifiedTime );
    }
}
