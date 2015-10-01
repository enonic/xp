package com.enonic.xp.core.impl.content.page.region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.enonic.xp.resource.ResourceNotFoundException;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlLayoutDescriptorParser;

abstract class AbstractGetLayoutDescriptorCommand<T extends AbstractGetLayoutDescriptorCommand>
{
    private final static Logger LOG = LoggerFactory.getLogger( AbstractGetLayoutDescriptorCommand.class );

    private final static String PATH = "/site/layouts";

    protected ApplicationService applicationService;

    protected ResourceService resourceService;

    private MixinService mixinService;

    protected final LayoutDescriptor getDescriptor( final DescriptorKey key )
    {
        final ResourceKey resourceKey = LayoutDescriptor.toResourceKey( key );
        final Resource resource = resourceService.getResource( resourceKey );
        resource.requireExists();
        final String descriptorXml = resource.readString();
        final LayoutDescriptor.Builder builder = LayoutDescriptor.create();

        try
        {
            parseXml( resourceKey.getApplicationKey(), builder, descriptorXml );
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load layout descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
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
        final ResourceKeys resourceKeys = this.resourceService.findFolders( application.getKey(), PATH );

        for ( final ResourceKey resourceKey : resourceKeys )
        {
            final DescriptorKey key = DescriptorKey.from( application.getKey(), resourceKey.getName() );
            try
            {
                final LayoutDescriptor layoutDescriptor = getDescriptor( key );
                layoutDescriptors.add( layoutDescriptor );
            }
            catch ( ResourceNotFoundException e )
            {
                // ignore layout descriptor if folder found but not xml
                LOG.warn( "Layout descriptor [" + key.toString() + "] not found" );
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
