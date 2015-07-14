package com.enonic.xp.core.impl.content.page.region;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.xp.form.InlineMixinsToFormItemsTransformer;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.module.Modules;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.Resources;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlLayoutDescriptorParser;

abstract class AbstractGetLayoutDescriptorCommand<T extends AbstractGetLayoutDescriptorCommand>
{
    private final static Pattern PATTERN = Pattern.compile( "/app/layouts/([^/]+)/\\1.xml" );

    protected ModuleService moduleService;

    protected ResourceService resourceService;

    private InlineMixinsToFormItemsTransformer inlineMixinsTransformer;

    protected final LayoutDescriptor getDescriptor( final DescriptorKey key )
    {
        final ResourceKey resourceKey = LayoutDescriptor.toResourceKey( key );
        final Resource resource = Resource.from( resourceKey );

        final String descriptorXml = resource.readString();
        final LayoutDescriptor.Builder builder = LayoutDescriptor.create();

        try
        {
            parseXml( resourceKey.getModule(), builder, descriptorXml );
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load layout descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
        }

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
        final Resources resources = this.resourceService.findResources( module.getKey(), PATTERN.toString() );

        for ( final Resource resource : resources )
        {
            Matcher matcher = PATTERN.matcher( resource.getKey().getPath() );
            if(matcher.matches())
            {
                final DescriptorKey key = DescriptorKey.from( module.getKey(), matcher.group( 1 ) );
                final LayoutDescriptor layoutDescriptor = getDescriptor( key );
                if ( layoutDescriptor != null )
                {
                    layoutDescriptors.add( layoutDescriptor );
                }
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

    @SuppressWarnings("unchecked")
    public final T resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        return (T) this;
    }
}
