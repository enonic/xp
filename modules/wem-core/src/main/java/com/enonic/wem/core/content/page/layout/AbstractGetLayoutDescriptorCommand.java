package com.enonic.wem.core.content.page.layout;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.api.xml.XmlSerializers;

abstract class AbstractGetLayoutDescriptorCommand<T extends AbstractGetLayoutDescriptorCommand>
{
    private final static Pattern PATTERN = Pattern.compile( "component/([^/]+)/layout.xml" );

    protected ModuleService moduleService;

    protected ResourceService resourceService;

    protected final LayoutDescriptor getDescriptor( final LayoutDescriptorKey key )
    {
        final ModuleResourceKey resourceKey = key.toResourceKey();
        final Resource resource = this.resourceService.getResource( resourceKey );

        final String descriptorXml = resource.readAsString();
        final LayoutDescriptor.Builder builder = LayoutDescriptor.newLayoutDescriptor();
        XmlSerializers.layoutDescriptor().parse( descriptorXml ).to( builder );
        builder.name( key.getName() ).key( key );

        return builder.build();
    }

    protected final LayoutDescriptors getDescriptorsFromModules( final Modules modules )
    {
        final LayoutDescriptors.Builder layoutDescriptors = LayoutDescriptors.newLayoutDescriptors();
        for ( final Module module : modules )
        {
            final Set<String> resources = module.getResourcePaths();
            final Collection<String> componentNames = Collections2.transform( resources, new Function<String, String>()
            {
                public String apply( final String input )
                {
                    final Matcher matcher = PATTERN.matcher( input );
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
                final LayoutDescriptorKey key = LayoutDescriptorKey.from( module.getKey(), descriptorName );
                final LayoutDescriptor layoutDescriptor = getDescriptor( key );
                if ( layoutDescriptor != null )
                {
                    layoutDescriptors.add( layoutDescriptor );
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
