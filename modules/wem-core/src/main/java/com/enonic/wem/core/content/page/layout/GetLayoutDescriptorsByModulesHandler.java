package com.enonic.wem.core.content.page.layout;

import java.io.IOException;

import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.layout.GetLayoutDescriptorsByModules;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.core.command.CommandHandler;

import static com.enonic.wem.api.command.Commands.page;

public class GetLayoutDescriptorsByModulesHandler
    extends CommandHandler<GetLayoutDescriptorsByModules>
{
    private static final ResourcePath COMPONENT_FOLDER = ResourcePath.from( "component" );

    private static final ResourcePath LAYOUT_DESCRIPTOR_XML = ResourcePath.from( "layout.xml" );

    private ModuleService moduleService;

    @Override
    public void handle()
        throws Exception
    {
        final ModuleKeys moduleKeys = this.command.getModuleKeys();
        final Modules modules = moduleService.getModules( moduleKeys );

        final LayoutDescriptors layoutDescriptors = getLayoutDescriptorsFromModules( modules );
        command.setResult( layoutDescriptors );
    }

    private LayoutDescriptors getLayoutDescriptorsFromModules( final Modules modules )
        throws IOException
    {
        final LayoutDescriptors.Builder layoutDescriptors = LayoutDescriptors.newLayoutDescriptors();
        for ( Module module : modules )
        {
            final ModuleFileEntry root = module.getModuleDirectoryEntry();
            if ( root == null || !root.contains( COMPONENT_FOLDER ) )
            {
                continue;
            }
            final ModuleFileEntry componentFolder = root.getEntry( COMPONENT_FOLDER );
            final Client client = context.getClient();
            for ( ModuleFileEntry descriptorFolder : componentFolder )
            {
                if ( descriptorFolder.contains( LAYOUT_DESCRIPTOR_XML ) )
                {
                    final ComponentDescriptorName descriptorName = new ComponentDescriptorName( descriptorFolder.getName() );
                    final LayoutDescriptorKey key = LayoutDescriptorKey.from( module.getModuleKey(), descriptorName );
                    final LayoutDescriptor layoutDescriptor = client.execute( page().descriptor().layout().getByKey( key ) );
                    if ( layoutDescriptor != null )
                    {
                        layoutDescriptors.add( layoutDescriptor );
                    }
                }
            }
        }

        return layoutDescriptors.build();
    }

    @Inject
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }
}
