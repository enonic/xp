package com.enonic.wem.core.content.page.image;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.content.page.image.ImageDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.api.resource.Resource2;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeys;
import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.xml.XmlSerializers;

abstract class AbstractGetImageDescriptorCommand<T extends AbstractGetImageDescriptorCommand>
{
    protected ModuleService moduleService;

    protected ResourceService resourceService;

    protected final ImageDescriptor getImageDescriptor( final ImageDescriptorKey key )
    {
        final ResourceKey resourceKey = key.toResourceKey();
        final Resource2 resource = this.resourceService.getResource( resourceKey );

        final String descriptorXml = resource.getAsString();
        final ImageDescriptor.Builder builder = ImageDescriptor.newImageDescriptor();
        XmlSerializers.imageDescriptor().parse( descriptorXml ).to( builder );
        builder.name( key.getName() ).key( key );

        return builder.build();
    }

    protected final ImageDescriptors getImageDescriptorsFromModules( final Modules modules )
    {
        final ImageDescriptors.Builder imageDescriptors = ImageDescriptors.newImageDescriptors();
        for ( final Module module : modules )
        {
            final ResourceKey componentFolder = ResourceKey.from( module.getModuleKey(), "component" );
            final ResourceKeys descriptorFolders = this.resourceService.getChildren( componentFolder );

            for ( final ResourceKey descriptorFolder : descriptorFolders )
            {
                final ResourceKey descriptorFile = descriptorFolder.resolve( "image.xml" );
                if ( this.resourceService.hasResource( descriptorFile ) )
                {
                    final ComponentDescriptorName descriptorName = new ComponentDescriptorName( descriptorFolder.getName() );
                    final ImageDescriptorKey key = ImageDescriptorKey.from( module.getModuleKey(), descriptorName );
                    final ImageDescriptor imageDescriptor = getImageDescriptor( key );
                    if ( imageDescriptor != null )
                    {
                        imageDescriptors.add( imageDescriptor );
                    }
                }
            }
        }

        return imageDescriptors.build();
    }

    @SuppressWarnings("unchecked")
    public final T moduleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final T resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        return (T) this;
    }
}
