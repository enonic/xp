package com.enonic.xp.core.impl.content.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinDescriptors;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinNames;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MixinServiceImplTest
    extends ApplicationTestSupport
{
    protected MixinServiceImpl service;

    protected ContentTypeServiceImpl contentTypeService;

    @Override
    protected void initialize()
    {
        this.service = new MixinServiceImpl( this.applicationService, this.resourceService );
        this.contentTypeService = new ContentTypeServiceImpl( this.resourceService, this.applicationService, null );
    }

    @Test
    void testEmpty()
    {
        final MixinDescriptors types1 = this.service.getAll();
        assertNotNull( types1 );
        assertEquals( 3, types1.getSize() );

        final MixinDescriptors types2 = this.service.getByApplication( ApplicationKey.from( "other" ) );
        assertNotNull( types2 );
        assertEquals( 0, types2.getSize() );

        final MixinDescriptor mixinDescriptor = service.getByName( MixinName.from( "other:mytype" ) );
        assertEquals( null, mixinDescriptor );
    }

    @Test
    void testSystemMixins()
    {
        MixinDescriptors mixins = service.getAll();
        assertNotNull( mixins );
        assertEquals( 3, mixins.getSize() );

        mixins = service.getByApplication( ApplicationKey.MEDIA_MOD );
        assertNotNull( mixins );
        assertEquals( 2, mixins.getSize() );

        MixinDescriptor mixinDescriptor = service.getByName( MediaInfo.GPS_INFO_METADATA_NAME );
        assertNotNull( mixinDescriptor );

        mixinDescriptor = service.getByName( MediaInfo.IMAGE_INFO_METADATA_NAME );
        assertNotNull( mixinDescriptor );

        mixinDescriptor = service.getByName( MediaInfo.CAMERA_INFO_METADATA_NAME );
        assertNotNull( mixinDescriptor );
    }

    @Test
    void testGetByNames()
    {
        MixinDescriptors mixinDescriptors = service.getByNames( MixinNames.from( MediaInfo.GPS_INFO_METADATA_NAME ) );
        assertEquals( 1, mixinDescriptors.getSize() );

        mixinDescriptors = service.getByNames( MixinNames.from( MediaInfo.GPS_INFO_METADATA_NAME, MediaInfo.IMAGE_INFO_METADATA_NAME ) );
        assertEquals( 2, mixinDescriptors.getSize() );

        mixinDescriptors = service.getByNames(
            MixinNames.from( MediaInfo.GPS_INFO_METADATA_NAME, MediaInfo.IMAGE_INFO_METADATA_NAME, MediaInfo.CAMERA_INFO_METADATA_NAME ) );
        assertEquals( 3, mixinDescriptors.getSize() );
    }

    @Test
    void testApplications()
    {
        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( "myapp2", "/apps/myapp2" );

        final MixinDescriptors types1 = this.service.getAll();
        assertNotNull( types1 );
        assertEquals( 9, types1.getSize() );

        final MixinDescriptors types2 = this.service.getByApplication( ApplicationKey.from( "myapp1" ) );
        assertNotNull( types2 );
        assertEquals( 2, types2.getSize() );

        final MixinDescriptors types3 = this.service.getByApplication( ApplicationKey.from( "myapp2" ) );
        assertNotNull( types3 );
        assertEquals( 4, types3.getSize() );

        final MixinDescriptor mixinDescriptor = service.getByName( MixinName.from( "myapp2:mixin1" ) );
        assertNotNull( mixinDescriptor );
    }
}
