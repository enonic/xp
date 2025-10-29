package com.enonic.xp.core.impl.site;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.CmsFormFragmentService;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.MixinMappings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CmsServiceImplTest
    extends ApplicationTestSupport
{
    protected CmsFormFragmentService formFragmentService;

    protected CmsServiceImpl service;

    @Override
    protected void initialize()
    {
        this.formFragmentService = mock( CmsFormFragmentService.class );
        when( this.formFragmentService.inlineFormItems( Mockito.any() ) ).thenAnswer( ( invocation ) -> invocation.getArguments()[0] );
        addApplication( "myapp", "/apps/myapp" );

        this.service = new CmsServiceImpl( resourceService, formFragmentService );
    }

    @Test
    void testGetDescriptor()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );
        final CmsDescriptor descriptor = this.service.getDescriptor( applicationKey );
        assertEquals( 1, descriptor.getForm().size() );
        assertEquals( 2, descriptor.getMixinMappings().getSize() );
        assertTrue( Instant.now().isAfter( descriptor.getModifiedTime() ) );
    }

    @Test
    void testGetPortalDescriptor()
    {
        final ApplicationKey applicationKey = ApplicationKey.PORTAL;
        final CmsDescriptor descriptor = this.service.getDescriptor( applicationKey );

        final Form formItems = descriptor.getForm();
        assertEquals( 1, formItems.size() );
        final Input baseUrl = formItems.getInput( "baseUrl" );
        assertThat( baseUrl ).extracting( Input::getInputType, Input::getLabel, Input::getLabelI18nKey )
            .containsExactly( InputTypeName.TEXT_LINE, "Base URL", "portal.baseUrl.label" );

        // MixinMappings checks
        final MixinMappings mixinMappings = descriptor.getMixinMappings();

        assertThat( mixinMappings.getNames() ).containsExactly( MediaInfo.IMAGE_INFO_METADATA_NAME, MediaInfo.CAMERA_INFO_METADATA_NAME,
                                                                MediaInfo.GPS_INFO_METADATA_NAME );
    }

    @Test
    void testGetDescriptorForUnknownApplication()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "unknown" );
        final CmsDescriptor descriptor = this.service.getDescriptor( applicationKey );
        assertNull( descriptor );
    }
}
