package com.enonic.xp.core.impl.site;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlSiteParser;

@Component(immediate = true)
public class SiteServiceImpl
    implements SiteService
{
    private ResourceService resourceService;

    private MixinService mixinService;

    @Override
    public SiteDescriptor getDescriptor( final ApplicationKey applicationKey )
    {
        final ResourceProcessor<ApplicationKey, SiteDescriptor> processor = newProcessor( applicationKey );
        final SiteDescriptor descriptor = this.resourceService.processResource( processor );

        if ( descriptor == null )
        {
            return null;
        }

        final Form form = mixinService.inlineFormItems( descriptor.getForm() );
        return SiteDescriptor.copyOf( descriptor ).
            form( form ).
            build();
    }

    private ResourceProcessor<ApplicationKey, SiteDescriptor> newProcessor( final ApplicationKey applicationKey )
    {
        return new ResourceProcessor.Builder<ApplicationKey, SiteDescriptor>().
            key( applicationKey ).
            segment( "siteDescriptor" ).
            keyTranslator( SiteDescriptor::toResourceKey ).
            processor( this::loadDescriptor ).
            build();
    }

    private SiteDescriptor loadDescriptor( final Resource resource )
    {
        final SiteDescriptor.Builder builder = SiteDescriptor.create();
        parseXml( resource, builder );
        return builder.build();
    }

    private void parseXml( final Resource resource, final SiteDescriptor.Builder builder )
    {
        try
        {
            new XmlSiteParser().
                siteDescriptorBuilder( builder ).
                currentApplication( resource.getKey().getApplicationKey() ).
                source( resource.readString() ).
                parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load site descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
        }
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }
}
