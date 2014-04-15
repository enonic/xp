package com.enonic.wem.core.content.page.image;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.content.page.image.ImageDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeys;
import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.xml.XmlSerializers;

abstract class AbstractGetImageDescriptorCommand<T extends AbstractGetImageDescriptorCommand>
{
    private final static Pattern PATTERN = Pattern.compile( "/component/([^/]+)/image.xml" );

    protected ModuleService moduleService;

    protected ResourceService resourceService;

    protected final ImageDescriptor getImageDescriptor( final ImageDescriptorKey key )
    {
        final ResourceKey resourceKey = key.toResourceKey();
        final Resource resource = this.resourceService.getResource( resourceKey );

        final String descriptorXml = resource.readAsString();
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
            final ResourceKeys children = this.resourceService.getChildren( componentFolder );
            final Collection<String> componentNames = children.transform( new Function<ResourceKey, String>()
            {
                public String apply( final ResourceKey input )
                {
                    final Matcher matcher = PATTERN.matcher( input.getPath() );
                    if ( matcher.matches() )
                    {
                        return matcher.group( 1 );
                    }

                    return null;
                }
            } );

            for ( final String componentName : componentNames )
            {
                final ComponentDescriptorName descriptorName = new ComponentDescriptorName( componentName );
                final ImageDescriptorKey key = ImageDescriptorKey.from( module.getModuleKey(), descriptorName );
                final ImageDescriptor imageDescriptor = getImageDescriptor( key );
                if ( imageDescriptor != null )
                {
                    imageDescriptors.add( imageDescriptor );
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
