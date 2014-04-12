package com.enonic.wem.core.content.page.part;

import java.io.IOException;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.api.resource.Resource2;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeys;
import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.xml.XmlSerializers;

abstract class AbstractGetPartDescriptorCommand<T extends AbstractGetPartDescriptorCommand>
{
    protected ModuleService moduleService;

    protected ResourceService resourceService;

    protected final PartDescriptor getDescriptor( final PartDescriptorKey key )
        throws IOException
    {
        final ResourceKey resourceKey = key.toResourceKey();
        final Resource2 resource = this.resourceService.getResource( resourceKey );

        final String descriptorXml = resource.getAsString();
        final PartDescriptor.Builder builder = PartDescriptor.newPartDescriptor();
        XmlSerializers.partDescriptor().parse( descriptorXml ).to( builder );
        builder.name( key.getName() ).key( key );

        return builder.build();
    }

    protected final PartDescriptors getDescriptorsFromModules( final Modules modules )
        throws IOException
    {
        final PartDescriptors.Builder partDescriptors = PartDescriptors.newPartDescriptors();
        for ( final Module module : modules )
        {
            final ResourceKey componentFolder = ResourceKey.from( module.getModuleKey(), "component" );
            final ResourceKeys descriptorFolders = this.resourceService.getChildren( componentFolder );

            for ( final ResourceKey descriptorFolder : descriptorFolders )
            {
                final ResourceKey descriptorFile = descriptorFolder.resolve( "part.xml" );
                if ( this.resourceService.hasResource( descriptorFile ) )
                {
                    final ComponentDescriptorName descriptorName = new ComponentDescriptorName( descriptorFolder.getName() );
                    final PartDescriptorKey key = PartDescriptorKey.from( module.getModuleKey(), descriptorName );
                    final PartDescriptor partDescriptor = getDescriptor( key );
                    if ( partDescriptor != null )
                    {
                        partDescriptors.add( partDescriptor );
                    }
                }
            }
        }

        return partDescriptors.build();
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
