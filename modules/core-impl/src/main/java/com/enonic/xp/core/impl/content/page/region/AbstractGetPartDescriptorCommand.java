package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlPartDescriptorParser;

abstract class AbstractGetPartDescriptorCommand<T extends AbstractGetPartDescriptorCommand>
{
    private final static String PATH = "/site/parts";

    protected ApplicationService applicationService;

    protected ResourceService resourceService;

    private MixinService mixinService;

    protected final PartDescriptor getDescriptor( final DescriptorKey key )
    {
        final ResourceKey resourceKey = PartDescriptor.toResourceKey( key );
        final Resource resource = resourceService.getResource( resourceKey );

        final PartDescriptor.Builder builder = PartDescriptor.create();

        if ( resource.exists() )
        {

            final String descriptorXml = resource.readString();
            try
            {
                parseXml( resourceKey.getApplicationKey(), builder, descriptorXml );
            }
            catch ( final Exception e )
            {
                throw new XmlException( e, "Could not load part descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
            }
        }
        else
        {
            builder.displayName( key.getName() ).
                config( Form.create().build() );
        }

        builder.name( key.getName() ).key( key );
        final PartDescriptor partDescriptor = builder.build();

        return PartDescriptor.copyOf( partDescriptor ).
            config( mixinService.inlineFormItems( partDescriptor.getConfig() ) ).
            build();
    }

    private void parseXml( final ApplicationKey applicationKey, final PartDescriptor.Builder builder, final String xml )
    {
        final XmlPartDescriptorParser parser = new XmlPartDescriptorParser();
        parser.builder( builder );
        parser.currentApplication( applicationKey );
        parser.source( xml );
        parser.parse();
    }

    protected final PartDescriptors getDescriptorsFromApplications( final Applications applications )
    {
        final PartDescriptors.Builder partDescriptors = PartDescriptors.create();
        for ( final Application application : applications )
        {
            readDescriptor( application, partDescriptors );
        }

        return partDescriptors.build();
    }

    protected final PartDescriptors getDescriptorsFromApplication( final Application application )
    {
        final PartDescriptors.Builder partDescriptors = PartDescriptors.create();
        readDescriptor( application, partDescriptors );
        return partDescriptors.build();
    }

    private void readDescriptor( final Application application, final PartDescriptors.Builder partDescriptors )
    {
        final ResourceKeys resourceKeys = this.resourceService.findResourceKeys( application.getKey(), PATH, "*", false );

        for ( final ResourceKey resourceKey : resourceKeys )
        {
            final String descriptorName = resourceKey.getName();
            final DescriptorKey key = DescriptorKey.from( application.getKey(), descriptorName );
            final PartDescriptor partDescriptor = getDescriptor( key );
            if ( partDescriptor != null )
            {
                partDescriptors.add( partDescriptor );
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
