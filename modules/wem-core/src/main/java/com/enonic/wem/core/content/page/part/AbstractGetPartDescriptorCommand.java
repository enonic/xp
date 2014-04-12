package com.enonic.wem.core.content.page.part;

import java.io.IOException;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.core.content.page.DescriptorKeyToModuleResourceKey;
import com.enonic.wem.xml.XmlSerializers;

abstract class AbstractGetPartDescriptorCommand<T extends AbstractGetPartDescriptorCommand>
{
    private static final ResourcePath COMPONENT_FOLDER = ResourcePath.from( "component" );

    private static final ResourcePath PART_DESCRIPTOR_XML = ResourcePath.from( "part.xml" );

    protected ModuleService moduleService;

    protected final PartDescriptor getDescriptor( final PartDescriptorKey key )
        throws IOException
    {
        final ModuleResourceKey moduleResourceKey = DescriptorKeyToModuleResourceKey.translate( key );
        final Resource resource = this.moduleService.getResource( moduleResourceKey );

        final String descriptorXml = resource.readAsString();
        final PartDescriptor.Builder builder = PartDescriptor.newPartDescriptor();
        XmlSerializers.partDescriptor().parse( descriptorXml ).to( builder );
        builder.name( key.getName() ).key( key );

        return builder.build();
    }

    protected final PartDescriptors getDescriptorsFromModules( final Modules modules )
        throws IOException
    {
        final PartDescriptors.Builder partDescriptors = PartDescriptors.newPartDescriptors();
        for ( Module module : modules )
        {
            final ModuleFileEntry root = module.getModuleDirectoryEntry();
            if ( root == null || !root.contains( COMPONENT_FOLDER ) )
            {
                continue;
            }
            final ModuleFileEntry componentFolder = root.getEntry( COMPONENT_FOLDER );
            for ( ModuleFileEntry descriptorFolder : componentFolder )
            {
                if ( descriptorFolder.contains( PART_DESCRIPTOR_XML ) )
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
}
