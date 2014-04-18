package com.enonic.wem.core.content.page.part;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeys;
import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.xml.XmlSerializers;

abstract class AbstractGetPartDescriptorCommand<T extends AbstractGetPartDescriptorCommand>
{
    private final static Pattern PATTERN = Pattern.compile( "/component/([^/]+)/part.xml" );

    protected ModuleService moduleService;

    protected ResourceService resourceService;

    protected final PartDescriptor getDescriptor( final PartDescriptorKey key )
    {
        final ResourceKey resourceKey = key.toResourceKey();
        final Resource resource = this.resourceService.getResource( resourceKey );

        final String descriptorXml = resource.readAsString();
        final PartDescriptor.Builder builder = PartDescriptor.newPartDescriptor();
        XmlSerializers.partDescriptor().parse( descriptorXml ).to( builder );
        builder.name( key.getName() ).key( key );

        return builder.build();
    }

    protected final PartDescriptors getDescriptorsFromModules( final Modules modules )
    {
        final PartDescriptors.Builder partDescriptors = PartDescriptors.newPartDescriptors();
        for ( final Module module : modules )
        {
            final ResourceKey componentFolder = ResourceKey.from( module.getKey(), "component" );
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
                final PartDescriptorKey key = PartDescriptorKey.from( module.getKey(), descriptorName );
                final PartDescriptor partDescriptor = getDescriptor( key );
                if ( partDescriptor != null )
                {
                    partDescriptors.add( partDescriptor );
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
