package com.enonic.xp.core.impl.site;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.form.FormItems;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.XDataMappings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SiteServiceImplTest
    extends ApplicationTestSupport
{
    protected MixinService mixinService;

    protected SiteServiceImpl service;

    @Override
    protected void initialize()
    {
        this.mixinService = mock( MixinService.class );
        when( this.mixinService.inlineFormItems( Mockito.any() ) ).
            thenAnswer( ( invocation ) -> invocation.getArguments()[0] );
        addApplication( "myapp", "/apps/myapp" );

        this.service = new SiteServiceImpl();
        this.service.setResourceService( this.resourceService );
        this.service.setMixinService( this.mixinService );
    }

    @Test
    public void get_descriptor()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );
        final SiteDescriptor siteDescriptor = this.service.getDescriptor( applicationKey );
        assertEquals( 1, siteDescriptor.getForm().getFormItems().size() );
        assertEquals( 2, siteDescriptor.getXDataMappings().getSize() );
        assertEquals( 2, siteDescriptor.getResponseProcessors().getSize() );
        assertEquals( "filter1", siteDescriptor.getResponseProcessors().get( 0 ).getName() );
        assertEquals( 20, siteDescriptor.getResponseProcessors().get( 1 ).getOrder() );
        assertTrue( Instant.now().isAfter( siteDescriptor.getModifiedTime() ) );
    }

    @Test
    public void get_portal_descriptor()
    {
        final ApplicationKey applicationKey = ApplicationKey.PORTAL;
        final SiteDescriptor siteDescriptor = this.service.getDescriptor( applicationKey );

        final FormItems formItems = siteDescriptor.getForm().getFormItems();
        assertEquals( 1, formItems.size() );
        final Input baseUrl = formItems.getItemByName( "baseUrl" ).toInput();
        assertThat( baseUrl ).extracting( Input::getInputType, Input::getLabel, Input::getLabelI18nKey )
            .containsExactly( InputTypeName.TEXT_LINE, "Base URL", "portal.baseUrl.label" );

        // XDataMappings checks
        final XDataMappings xdataMappings = siteDescriptor.getXDataMappings();

        assertThat( xdataMappings.getNames() ).containsExactly( MediaInfo.IMAGE_INFO_METADATA_NAME, MediaInfo.CAMERA_INFO_METADATA_NAME,
                                                                MediaInfo.GPS_INFO_METADATA_NAME );
    }

    @Test
    public void get_descriptor_for_unknown_application()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "unknown" );
        final SiteDescriptor siteDescriptor = this.service.getDescriptor( applicationKey );
        assertNull( siteDescriptor );
    }
}
