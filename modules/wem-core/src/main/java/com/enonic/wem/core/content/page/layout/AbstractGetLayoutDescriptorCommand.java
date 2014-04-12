package com.enonic.wem.core.content.page.layout;

import java.io.IOException;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.core.content.page.DescriptorKeyToModuleResourceKey;
import com.enonic.wem.xml.XmlSerializers;

abstract class AbstractGetLayoutDescriptorCommand<T extends AbstractGetLayoutDescriptorCommand>
{
    private static final ResourcePath COMPONENT_FOLDER = ResourcePath.from( "component" );

    private static final ResourcePath LAYOUT_DESCRIPTOR_XML = ResourcePath.from( "layout.xml" );

    protected ModuleService moduleService;

    protected final LayoutDescriptor getDescriptor( final LayoutDescriptorKey key )
        throws IOException
    {
        final ModuleResourceKey moduleResourceKey = DescriptorKeyToModuleResourceKey.translate( key );
        final Resource resource = this.moduleService.getResource( moduleResourceKey );

        final String descriptorXml = resource.readAsString();
        final LayoutDescriptor.Builder builder = LayoutDescriptor.newLayoutDescriptor();
        XmlSerializers.layoutDescriptor().parse( descriptorXml ).to( builder );
        builder.name( key.getName() ).key( key );

        return builder.build();
    }

    protected final LayoutDescriptors getDescriptorsFromModules( final Modules modules )
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
                    final LayoutDescriptor layoutDescriptor = getDescriptor( key );
                    if ( layoutDescriptor != null )
                    {
                        layoutDescriptors.add( layoutDescriptor );
                    }
                }
            }
        }

        return layoutDescriptors.build();
    }

    @SuppressWarnings("unchecked")
    public final T moduleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
        return (T) this;
    }
}
