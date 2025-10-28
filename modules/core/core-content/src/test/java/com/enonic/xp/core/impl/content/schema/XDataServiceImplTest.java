package com.enonic.xp.core.impl.content.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.xdata.MixinDescriptor;
import com.enonic.xp.schema.xdata.MixinName;
import com.enonic.xp.schema.xdata.MixinNames;
import com.enonic.xp.schema.xdata.MixinDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class XDataServiceImplTest
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

        final MixinDescriptor xData = service.getByName( MixinName.from( "other:mytype" ) );
        assertEquals( null, xData );
    }

    @Test
    void testSystemXDatas()
    {
        MixinDescriptors xDatas = service.getAll();
        assertNotNull( xDatas );
        assertEquals( 3, xDatas.getSize() );

        xDatas = service.getByApplication( ApplicationKey.MEDIA_MOD );
        assertNotNull( xDatas );
        assertEquals( 2, xDatas.getSize() );

        MixinDescriptor xData = service.getByName( MediaInfo.GPS_INFO_METADATA_NAME );
        assertNotNull( xData );

        xData = service.getByName( MediaInfo.IMAGE_INFO_METADATA_NAME );
        assertNotNull( xData );

        xData = service.getByName( MediaInfo.CAMERA_INFO_METADATA_NAME );
        assertNotNull( xData );
    }

    @Test
    void testGetByNames()
    {
        MixinDescriptors xDatas = service.getByNames( MixinNames.from( MediaInfo.GPS_INFO_METADATA_NAME ) );
        assertEquals( 1, xDatas.getSize() );

        xDatas = service.getByNames( MixinNames.from( MediaInfo.GPS_INFO_METADATA_NAME, MediaInfo.IMAGE_INFO_METADATA_NAME ) );
        assertEquals( 2, xDatas.getSize() );

        xDatas = service.getByNames(
            MixinNames.from( MediaInfo.GPS_INFO_METADATA_NAME, MediaInfo.IMAGE_INFO_METADATA_NAME, MediaInfo.CAMERA_INFO_METADATA_NAME ) );
        assertEquals( 3, xDatas.getSize() );
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

        final MixinDescriptor xData = service.getByName( MixinName.from( "myapp2:xdata1" ) );
        assertNotNull( xData );
    }
}
