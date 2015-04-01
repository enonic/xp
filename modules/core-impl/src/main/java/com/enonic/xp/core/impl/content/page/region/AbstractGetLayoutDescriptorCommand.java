package com.enonic.xp.core.impl.content.page.region;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Collections2;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.content.page.region.LayoutDescriptor;
import com.enonic.xp.content.page.region.LayoutDescriptors;
import com.enonic.xp.form.InlineMixinsToFormItemsTransformer;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.module.Modules;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.xml.parser.XmlLayoutDescriptorParser;

abstract class AbstractGetLayoutDescriptorCommand<T extends AbstractGetLayoutDescriptorCommand>
{
    private final static Pattern PATTERN = Pattern.compile( "cms/layouts/([^/]+)/layout.xml" );

    protected ModuleService moduleService;

    private InlineMixinsToFormItemsTransformer inlineMixinsTransformer;

    protected final LayoutDescriptor getDescriptor( final DescriptorKey key )
    {
        final ResourceKey resourceKey = LayoutDescriptor.toResourceKey( key );
        final Resource resource = Resource.from( resourceKey );

        final String descriptorXml = resource.readString();
        final LayoutDescriptor.Builder builder = LayoutDescriptor.create();

        parseXml( resourceKey.getModule(), builder, descriptorXml );

        builder.name( key.getName() ).key( key );
        final LayoutDescriptor layoutDescriptor = builder.build();

        return LayoutDescriptor.copyOf( layoutDescriptor ).
            config( inlineMixinsTransformer.transformForm( layoutDescriptor.getConfig() ) ).
            build();
    }

    private void parseXml( final ModuleKey moduleKey, final LayoutDescriptor.Builder builder, final String xml )
    {
        final XmlLayoutDescriptorParser parser = new XmlLayoutDescriptorParser();
        parser.builder( builder );
        parser.currentModule( moduleKey );
        parser.source( xml );
        parser.parse();
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

    @SuppressWarnings("unchecked")
    public final T mixinService( final MixinService mixinService )
    {
        this.inlineMixinsTransformer = new InlineMixinsToFormItemsTransformer( mixinService );
        return (T) this;
    }
}
