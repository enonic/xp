package com.enonic.xp.core.impl.site;

import java.time.Instant;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.content.parser.YmlCmsDescriptorParser;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.site.XDataMapping;
import com.enonic.xp.site.XDataMappings;

import static com.enonic.xp.media.MediaInfo.CAMERA_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.GPS_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_METADATA_NAME;

@Component(immediate = true)
public class CmsServiceImpl
    implements CmsService
{
    private static final CmsDescriptor PORTAL_CMS_DESCRIPTOR = CmsDescriptor.create()
        .applicationKey( ApplicationKey.PORTAL )
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

    private final ResourceService resourceService;

    private final MixinService mixinService;

    @Activate
    public CmsServiceImpl( @Reference final ResourceService resourceService, @Reference final MixinService mixinService )
    {
        this.resourceService = resourceService;
        this.mixinService = mixinService;
    }

    @Override
    public CmsDescriptor getDescriptor( ApplicationKey applicationKey )
    {
        if ( ApplicationKey.PORTAL.equals( applicationKey ) )
        {
            return PORTAL_CMS_DESCRIPTOR;
        }

        final ResourceProcessor<ApplicationKey, CmsDescriptor> processor = newDescriptorProcessor( applicationKey );
        final CmsDescriptor descriptor = this.resourceService.processResource( processor );

        if ( descriptor == null )
        {
            return null;
        }

        return CmsDescriptor.copyOf( descriptor )
            .applicationKey( applicationKey )
            .form( mixinService.inlineFormItems( descriptor.getForm() ) )
            .build();
    }

    private ResourceProcessor<ApplicationKey, CmsDescriptor> newDescriptorProcessor( final ApplicationKey applicationKey )
    {
        return new ResourceProcessor.Builder<ApplicationKey, CmsDescriptor>().key( applicationKey )
            .segment( "cmsDescriptor" )
            .keyTranslator( CmsDescriptor::toResourceKey )
            .processor( this::loadDescriptor )
            .build();
    }

    private CmsDescriptor loadDescriptor( final Resource resource )
    {
        return YmlCmsDescriptorParser.parse( resource.readString(), resource.getKey().getApplicationKey() )
            .modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) )
            .build();
    }
}
