package com.enonic.wem.core.content.page.part;

import java.io.IOException;

import javax.inject.Inject;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.part.GetPartDescriptorsByModules;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptorService;
import com.enonic.wem.api.content.page.part.PartDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.core.command.CommandHandler;

public class GetPartDescriptorsByModulesHandler
    extends CommandHandler<GetPartDescriptorsByModules>
{
    private static final ResourcePath COMPONENT_FOLDER = ResourcePath.from( "component" );

    private static final ResourcePath PART_DESCRIPTOR_XML = ResourcePath.from( "part.xml" );

    private ModuleService moduleService;

    private PartDescriptorService partDescriptorService;

    @Override
    public void handle()
        throws Exception
    {
        final ModuleKeys moduleKeys = this.command.getModuleKeys();
        final Modules modules = moduleService.getModules( moduleKeys );

        final PartDescriptors partDescriptors = getPartDescriptorsFromModules( modules );
        command.setResult( partDescriptors );
    }

    private PartDescriptors getPartDescriptorsFromModules( final Modules modules )
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
                    final PartDescriptor partDescriptor = partDescriptorService.getByKey( key );
                    if ( partDescriptor != null )
                    {
                        partDescriptors.add( partDescriptor );
                    }
                }
            }
        }

        return partDescriptors.build();
    }

    @Inject
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }

    @Inject
    public void setPartDescriptorService( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
    }
}
