package com.enonic.xp.core.impl.schema.xdata;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.AbstractSchemaTest;
import com.enonic.xp.core.impl.schema.content.ContentTypeServiceImpl;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataNames;
import com.enonic.xp.schema.xdata.XDatas;

import static org.junit.Assert.*;

public class XDataServiceImplTest
    extends AbstractSchemaTest
{
    protected XDataServiceImpl service;

    protected ContentTypeServiceImpl contentTypeService;

    @Override
    protected void initialize()
        throws Exception
    {
        this.service = new XDataServiceImpl();
        this.service.setApplicationService( this.applicationService );
        this.service.setResourceService( this.resourceService );

        this.contentTypeService = new ContentTypeServiceImpl();
        this.contentTypeService.setResourceService( this.resourceService );
        this.contentTypeService.setApplicationService( this.applicationService );
    }

    @Test
    public void testEmpty()
    {
        final XDatas types1 = this.service.getAll();
        assertNotNull( types1 );
        assertEquals( 3, types1.getSize() );

        final XDatas types2 = this.service.getByApplication( ApplicationKey.from( "other" ) );
        assertNotNull( types2 );
        assertEquals( 0, types2.getSize() );

        final XData xData = service.getByName( XDataName.from( "other:mytype" ) );
        assertEquals( null, xData );
    }

    @Test
    public void testSystemMixins()
    {
        XDatas xDatas = service.getAll();
        assertNotNull( xDatas );
        assertEquals( 3, xDatas.getSize() );

        xDatas = service.getByApplication( ApplicationKey.MEDIA_MOD );
        assertNotNull( xDatas );
        assertEquals( 2, xDatas.getSize() );

        XData xData = service.getByName( MediaInfo.GPS_INFO_METADATA_NAME );
        assertNotNull( xData );

        xData = service.getByName( MediaInfo.IMAGE_INFO_METADATA_NAME );
        assertNotNull( xData );

        xData = service.getByName( MediaInfo.CAMERA_INFO_METADATA_NAME );
        assertNotNull( xData );
    }

    @Test
    public void testGetByContentType()
    {
        initializeApps();

        final ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "myapp2:address" ).
            metadata( XDataNames.from( "myapp2:address", "myapp2:address1" ) ).
            build();

        final XDatas xDatas = service.getFromContentType( contentType );
        assertNotNull( xDatas );
        assertEquals( 1, xDatas.getSize() );
    }

    @Test
    public void testGetByNames()
    {
        XDatas xDatas = service.getByNames( XDataNames.from( MediaInfo.GPS_INFO_METADATA_NAME ) );
        assertEquals( 1, xDatas.getSize() );

        xDatas = service.getByNames( XDataNames.from( MediaInfo.GPS_INFO_METADATA_NAME, MediaInfo.IMAGE_INFO_METADATA_NAME ) );
        assertEquals( 2, xDatas.getSize() );

        xDatas = service.getByNames(
            XDataNames.from( MediaInfo.GPS_INFO_METADATA_NAME, MediaInfo.IMAGE_INFO_METADATA_NAME, MediaInfo.CAMERA_INFO_METADATA_NAME ) );
        assertEquals( 3, xDatas.getSize() );
    }

    @Test
    public void testApplications()
    {
        initializeApps();

        final XDatas types1 = this.service.getAll();
        assertNotNull( types1 );
        assertEquals( 9, types1.getSize() );

        final XDatas types2 = this.service.getByApplication( ApplicationKey.from( "myapp1" ) );
        assertNotNull( types2 );
        assertEquals( 2, types2.getSize() );

        final XDatas types3 = this.service.getByApplication( ApplicationKey.from( "myapp2" ) );
        assertNotNull( types3 );
        assertEquals( 4, types3.getSize() );

        final XData xData = service.getByName( XDataName.from( "myapp2:xdata1" ) );
        assertNotNull( xData );
    }
}
