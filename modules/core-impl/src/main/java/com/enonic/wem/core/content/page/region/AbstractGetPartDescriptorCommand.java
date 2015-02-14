package com.enonic.wem.core.content.page.region;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Collections2;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.region.PartDescriptor;
import com.enonic.wem.api.content.page.region.PartDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.xml.mapper.XmlPartDescriptorMapper;
import com.enonic.wem.api.xml.model.XmlPartDescriptor;
import com.enonic.wem.api.xml.serializer.XmlSerializers;

abstract class AbstractGetPartDescriptorCommand<T extends AbstractGetPartDescriptorCommand>
{
    private final static Pattern PATTERN = Pattern.compile( "cms/parts/([^/]+)/part.xml" );

    protected ModuleService moduleService;

    protected final PartDescriptor getDescriptor( final DescriptorKey key )
    {
        final ResourceKey resourceKey = PartDescriptor.toResourceKey( key );
        final Resource resource = Resource.from( resourceKey );

        final String descriptorXml = resource.readString();
        final PartDescriptor.Builder builder = PartDescriptor.newPartDescriptor();

        final XmlPartDescriptor xmlObject = XmlSerializers.partDescriptor().parse( descriptorXml );
        new XmlPartDescriptorMapper( resourceKey.getModule() ).fromXml( xmlObject, builder );

        builder.name( key.getName() ).key( key );
        return builder.build();
    }

    protected final PartDescriptors getDescriptorsFromModules( final Modules modules )
    {
        final PartDescriptors.Builder partDescriptors = PartDescriptors.newPartDescriptors();
        for ( final Module module : modules )
        {
            readDescriptor( module, partDescriptors );
        }

        return partDescriptors.build();
    }

    protected final PartDescriptors getDescriptorsFromModule( final Module module )
    {
        final PartDescriptors.Builder partDescriptors = PartDescriptors.newPartDescriptors();
        readDescriptor( module, partDescriptors );
        return partDescriptors.build();
    }

    private void readDescriptor( final Module module, final PartDescriptors.Builder partDescriptors )
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
            final DescriptorKey key = DescriptorKey.from( module.getKey(), componentName );
            final PartDescriptor partDescriptor = getDescriptor( key );
            if ( partDescriptor != null )
            {
                partDescriptors.add( partDescriptor );
            }
        }
    }

    @SuppressWarnings("unchecked")
    public final T moduleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
        return (T) this;
    }
}
