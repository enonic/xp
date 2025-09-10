package com.enonic.xp.core.impl.site;

import java.time.Instant;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.XDataMapping;
import com.enonic.xp.site.XDataMappings;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlSiteParser;

import static com.enonic.xp.media.MediaInfo.CAMERA_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.GPS_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_METADATA_NAME;

@Component(immediate = true)
public class SiteServiceImpl
    implements SiteService
{
    private static final SiteDescriptor PORTAL_SITE_DESCRIPTOR = SiteDescriptor.create()
        .xDataMappings( XDataMappings.create()
                            .add( XDataMapping.create().xDataName( IMAGE_INFO_METADATA_NAME ).allowContentTypes( "media:image" ).build() )
                            .add( XDataMapping.create().xDataName( CAMERA_INFO_METADATA_NAME ).allowContentTypes( "media:image" ).build() )
                            .add( XDataMapping.create().xDataName( GPS_INFO_METADATA_NAME ).allowContentTypes( "media:image" ).build() )
                            .build() )
        .form( Form.create()
                   .addFormItem( Input.create()
                                     .name( "baseUrl" )
                                     .label( "Base URL" )
                                     .labelI18nKey( "portal.baseUrl.label" )
                                     .inputType( InputTypeName.TEXT_LINE )
                                     .build() )
                   .build() )
        .build();

    private ResourceService resourceService;

    private MixinService mixinService;

    @Override
    public SiteDescriptor getDescriptor( final ApplicationKey applicationKey )
    {
        if ( ApplicationKey.PORTAL.equals( applicationKey ) )
        {
            return PORTAL_SITE_DESCRIPTOR;
        }

        final ResourceProcessor<ApplicationKey, SiteDescriptor> processor = newProcessor( applicationKey );
        final SiteDescriptor descriptor = this.resourceService.processResource( processor );

        if ( descriptor == null )
        {
            return null;
        }

        return SiteDescriptor.copyOf( descriptor ).form( mixinService.inlineFormItems( descriptor.getForm() ) ).build();
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
        builder.applicationKey( resource.getKey().getApplicationKey() );

        parseXml( resource, builder );

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        builder.modifiedTime( modifiedTime );

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
            throw new XmlException( e, "Could not load site descriptor [" + resource.getKey() + "]: " + e.getMessage() );
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
