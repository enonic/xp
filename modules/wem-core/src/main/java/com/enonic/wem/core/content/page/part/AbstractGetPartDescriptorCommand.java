package com.enonic.wem.core.content.page.part;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Collections2;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.api.xml.mapper.XmlPartDescriptorMapper;
import com.enonic.wem.api.xml.model.XmlPartDescriptor;
import com.enonic.wem.api.xml.serializer.XmlSerializers2;

abstract class AbstractGetPartDescriptorCommand<T extends AbstractGetPartDescriptorCommand>
{
    private final static Pattern PATTERN = Pattern.compile( "component/([^/]+)/part.xml" );

    protected ModuleService moduleService;

    protected ResourceService resourceService;

    protected final PartDescriptor getDescriptor( final PartDescriptorKey key )
    {
        final ResourceKey resourceKey = key.toResourceKey();
        final Resource resource = this.resourceService.getResource( resourceKey );

        final String descriptorXml = resource.readString();
        final PartDescriptor.Builder builder = PartDescriptor.newPartDescriptor();

        final XmlPartDescriptor xmlObject = XmlSerializers2.partDescriptor().parse( descriptorXml );
        XmlPartDescriptorMapper.fromXml( xmlObject, builder );

        builder.name( key.getName() ).key( key );
        return builder.build();
    }

    protected final PartDescriptors getDescriptorsFromModules( final Modules modules )
    {
        final PartDescriptors.Builder partDescriptors = PartDescriptors.newPartDescriptors();
        for ( final Module module : modules )
        {
            final Set<String> resources = module.getResourcePaths();
            final Collection<String> componentNames = Collections2.transform( resources, input -> {
                final Matcher matcher = PATTERN.matcher( input );
                if ( matcher.matches() )
                {
                    return matcher.group( 1 );
                }

                return null;
            } );

            for ( final String componentName : componentNames )
            {
                if ( componentName == null )
                {
                    continue;
                }
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
