package com.enonic.wem.core.content.page.layout;

import java.io.IOException;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.api.resource.Resource2;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeys;
import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.xml.XmlSerializers;

abstract class AbstractGetLayoutDescriptorCommand<T extends AbstractGetLayoutDescriptorCommand>
{
    protected ModuleService moduleService;

    protected ResourceService resourceService;

    protected final LayoutDescriptor getDescriptor( final LayoutDescriptorKey key )
        throws IOException
    {
        final ResourceKey resourceKey = key.toResourceKey();
        final Resource2 resource = this.resourceService.getResource( resourceKey );

        final String descriptorXml = resource.getAsString();
        final LayoutDescriptor.Builder builder = LayoutDescriptor.newLayoutDescriptor();
        XmlSerializers.layoutDescriptor().parse( descriptorXml ).to( builder );
        builder.name( key.getName() ).key( key );

        return builder.build();
    }

    protected final LayoutDescriptors getDescriptorsFromModules( final Modules modules )
        throws IOException
    {
        final LayoutDescriptors.Builder layoutDescriptors = LayoutDescriptors.newLayoutDescriptors();
        for ( final Module module : modules )
        {
            final ResourceKey componentFolder = ResourceKey.from( module.getModuleKey(), "component" );
            final ResourceKeys descriptorFolders = this.resourceService.getChildren( componentFolder );

            for ( final ResourceKey descriptorFolder : descriptorFolders )
            {
                final ResourceKey descriptorFile = descriptorFolder.resolve( "layout.xml" );
                if ( this.resourceService.hasResource( descriptorFile ) )
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

    @SuppressWarnings("unchecked")
    public final T resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        return (T) this;
    }
}
