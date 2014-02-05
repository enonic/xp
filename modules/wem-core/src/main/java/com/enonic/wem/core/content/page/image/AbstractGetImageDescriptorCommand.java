package com.enonic.wem.core.content.page.image;

import java.io.IOException;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.content.page.image.ImageDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.core.content.page.DescriptorKeyToModuleResourceKey;
import com.enonic.wem.xml.XmlSerializers;

abstract class AbstractGetImageDescriptorCommand
{
    private static final ResourcePath COMPONENT_FOLDER = ResourcePath.from( "component" );

    private static final ResourcePath IMAGE_DESCRIPTOR_XML = ResourcePath.from( "image.xml" );

    protected ModuleService moduleService;

    protected ImageDescriptor getImageDescriptor( final ImageDescriptorKey key )
        throws IOException
    {
        final ModuleResourceKey moduleResourceKey = DescriptorKeyToModuleResourceKey.translate( key );
        final Resource resource = this.moduleService.getResource( moduleResourceKey );

        final String descriptorXml = resource.readAsString();
        final ImageDescriptor.Builder builder = ImageDescriptor.newImageDescriptor();
        XmlSerializers.imageDescriptor().parse( descriptorXml ).to( builder );
        builder.name( key.getName() ).key( key );

        return builder.build();
    }

    protected ImageDescriptors getImageDescriptorsFromModules( final Modules modules )
        throws IOException
    {
        final ImageDescriptors.Builder imageDescriptors = ImageDescriptors.newImageDescriptors();
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
                if ( descriptorFolder.contains( IMAGE_DESCRIPTOR_XML ) )
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

}
