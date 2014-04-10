package com.enonic.wem.core.content.page.layout;

import java.io.IOException;

import com.google.common.base.Throwables;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorService;
import com.enonic.wem.api.content.page.layout.LayoutDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.api.module.ResourcePath;

final class GetLayoutDescriptorsByModulesCommand
{
    private static final ResourcePath COMPONENT_FOLDER = ResourcePath.from( "component" );

    private static final ResourcePath LAYOUT_DESCRIPTOR_XML = ResourcePath.from( "layout.xml" );

    private ModuleKeys moduleKeys;

    private ModuleService moduleService;

    private LayoutDescriptorService layoutDescriptorService;

    public LayoutDescriptors execute()
    {
        try
        {
            final Modules modules = this.moduleService.getModules( this.moduleKeys );
            return getLayoutDescriptorsFromModules( modules );
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
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
            for ( ModuleFileEntry descriptorFolder : componentFolder )
            {
                if ( descriptorFolder.contains( LAYOUT_DESCRIPTOR_XML ) )
                {
                    final ComponentDescriptorName descriptorName = new ComponentDescriptorName( descriptorFolder.getName() );
                    final LayoutDescriptorKey key = LayoutDescriptorKey.from( module.getModuleKey(), descriptorName );
                    final LayoutDescriptor layoutDescriptor = layoutDescriptorService.getByKey( key );
                    if ( layoutDescriptor != null )
                    {
                        layoutDescriptors.add( layoutDescriptor );
                    }
                }
            }
        }

        return layoutDescriptors.build();
    }

    public GetLayoutDescriptorsByModulesCommand moduleKeys( final ModuleKeys moduleKeys )
    {
        this.moduleKeys = moduleKeys;
        return this;
    }

    public GetLayoutDescriptorsByModulesCommand moduleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
        return this;
    }

    public GetLayoutDescriptorsByModulesCommand layoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
        return this;
    }
}
