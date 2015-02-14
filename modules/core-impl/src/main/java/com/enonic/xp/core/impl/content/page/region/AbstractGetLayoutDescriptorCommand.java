package com.enonic.xp.core.impl.content.page.region;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Collections2;

import com.enonic.xp.core.content.page.DescriptorKey;
import com.enonic.xp.core.content.page.region.LayoutDescriptor;
import com.enonic.xp.core.content.page.region.LayoutDescriptors;
import com.enonic.xp.core.module.Module;
import com.enonic.xp.core.module.ModuleService;
import com.enonic.xp.core.module.Modules;
import com.enonic.xp.core.resource.Resource;
import com.enonic.xp.core.resource.ResourceKey;
import com.enonic.xp.core.xml.mapper.XmlLayoutDescriptorMapper;
import com.enonic.xp.core.xml.model.XmlLayoutDescriptor;
import com.enonic.xp.core.xml.serializer.XmlSerializers;

abstract class AbstractGetLayoutDescriptorCommand<T extends AbstractGetLayoutDescriptorCommand>
{
    private final static Pattern PATTERN = Pattern.compile( "cms/layouts/([^/]+)/layout.xml" );

    protected ModuleService moduleService;

    protected final LayoutDescriptor getDescriptor( final DescriptorKey key )
    {
        final ResourceKey resourceKey = LayoutDescriptor.toResourceKey( key );
        final Resource resource = Resource.from( resourceKey );

        final String descriptorXml = resource.readString();
        final LayoutDescriptor.Builder builder = LayoutDescriptor.newLayoutDescriptor();

        final XmlLayoutDescriptor xmlObject = XmlSerializers.layoutDescriptor().parse( descriptorXml );
        new XmlLayoutDescriptorMapper( resourceKey.getModule() ).fromXml( xmlObject, builder );

        builder.name( key.getName() ).key( key );
        return builder.build();
    }

    protected final LayoutDescriptors getDescriptorsFromModules( final Modules modules )
    {
        final LayoutDescriptors.Builder layoutDescriptors = LayoutDescriptors.newLayoutDescriptors();
        for ( final Module module : modules )
        {
            readDescriptor( module, layoutDescriptors );
        }
        return layoutDescriptors.build();
    }

    protected final LayoutDescriptors getDescriptorsFromModule( final Module module )
    {
        final LayoutDescriptors.Builder layoutDescriptors = LayoutDescriptors.newLayoutDescriptors();
        readDescriptor( module, layoutDescriptors );
        return layoutDescriptors.build();
    }

    private void readDescriptor( final Module module, final LayoutDescriptors.Builder layoutDescriptors )
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
            final LayoutDescriptor layoutDescriptor = getDescriptor( key );
            if ( layoutDescriptor != null )
            {
                layoutDescriptors.add( layoutDescriptor );
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
