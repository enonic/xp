package com.enonic.xp.core.impl.content.page.region;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlLayoutDescriptorParser;

abstract class AbstractGetLayoutDescriptorCommand<T extends AbstractGetLayoutDescriptorCommand>
{
    private final static String PATH = "/site/layouts";

    private final static Pattern PATTERN = Pattern.compile( PATH + "/([^/]+)/\\1.xml" );

    protected ApplicationService applicationService;

    protected ResourceService resourceService;

    private MixinService mixinService;

    protected final LayoutDescriptor getDescriptor( final DescriptorKey key )
    {
        final ResourceKey resourceKey = LayoutDescriptor.toResourceKey( key );
        final Resource resource = resourceService.getResource( resourceKey );


        final LayoutDescriptor.Builder builder = LayoutDescriptor.create();

        if ( resource.exists() )
        {
            final String descriptorXml = resource.readString();
            try
            {
                parseXml( resourceKey.getApplicationKey(), builder, descriptorXml );
            }
            catch ( final Exception e )
            {
                throw new XmlException( e, "Could not load layout descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
            }
        }
        else {
            return null;
        }


        builder.name( key.getName() ).key( key );
        final LayoutDescriptor layoutDescriptor = builder.build();

        return LayoutDescriptor.copyOf( layoutDescriptor ).
            config( mixinService.inlineFormItems( layoutDescriptor.getConfig() ) ).
            build();
    }

    private void parseXml( final ApplicationKey applicationKey, final LayoutDescriptor.Builder builder, final String xml )
    {
        final XmlLayoutDescriptorParser parser = new XmlLayoutDescriptorParser();
        parser.builder( builder );
        parser.currentApplication( applicationKey );
        parser.source( xml );
        parser.parse();
    }

    protected final LayoutDescriptors getDescriptorsFromApplications( final Applications applications )
    {
        final LayoutDescriptors.Builder layoutDescriptors = LayoutDescriptors.create();
        for ( final Application application : applications )
        {
            readDescriptor( application, layoutDescriptors );
        }
        return layoutDescriptors.build();
    }

    protected final LayoutDescriptors getDescriptorsFromApplication( final Application application )
    {
        final LayoutDescriptors.Builder layoutDescriptors = LayoutDescriptors.create();
        readDescriptor( application, layoutDescriptors );
        return layoutDescriptors.build();
    }

    private void readDescriptor( final Application application, final LayoutDescriptors.Builder layoutDescriptors )
    {
        final ResourceKeys resourceKeys = this.resourceService.findResourceKeys( application.getKey(), PATH, "*.xml", true );

        for ( final ResourceKey resourceKey : resourceKeys )
        {
            Matcher matcher = PATTERN.matcher( resourceKey.getPath() );
            if ( matcher.matches() )
            {
                final DescriptorKey key = DescriptorKey.from( application.getKey(), matcher.group( 1 ) );
                final LayoutDescriptor layoutDescriptor = getDescriptor( key );
                if ( layoutDescriptor != null )
                {
                    layoutDescriptors.add( layoutDescriptor );
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public final T applicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final T mixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final T resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        return (T) this;
    }
}
