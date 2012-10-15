package com.enonic.wem.web.rest.resource.upload;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.BodyPartEntity;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.AbstractResourceTest;
import com.enonic.wem.web.rest.service.upload.UploadItem;
import com.enonic.wem.web.rest.service.upload.UploadService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.when;

public class UploadResourceTest
    extends AbstractResourceTest
{
    private UploadResource resource;

    private FormDataMultiPart parts;

    private UploadService service;

    @Before
    public void setUp()
    {
        this.parts = new FormDataMultiPart();

        this.service = Mockito.mock( UploadService.class );
        this.resource = new UploadResource();
        this.resource.setUploadService( this.service );
    }

    @Test
    public void testUpload_noParts()
        throws Exception
    {
        final JsonResult result = this.resource.upload( this.parts );
        assertJsonResult( "upload_empty.json", result );
    }

    @Test
    public void testUpload_onePart()
        throws Exception
    {
        addFile( "1", "text.txt", new byte[10], "text/plain" );
        final JsonResult result = this.resource.upload( this.parts );
        assertJsonResult( "upload_one_part.json", result );
    }

    @Test
    public void testUpload_twoParts()
        throws Exception
    {
        addFile( "1", "text.txt", new byte[10], "text/plain" );
        addFile( "2", "image.png", new byte[20], "image/png" );
        final JsonResult result = this.resource.upload( this.parts );
        assertJsonResult( "upload_two_parts.json", result );
    }

    @Test
    public void testGetUploadedContentNotFound()
        throws Exception
    {
        Response response = this.resource.getUploadedContent( "b8328bb4-6c57-475a-8697-96de97a1bf39" );
        assertNull( response );
    }

    @Test
    public void testGetUploadedContent()
        throws Exception
    {
        final String id = "b8328bb4-6c57-475a-8697-96de97a1bf39";
        final byte[] data = "data".getBytes();
        final UploadItem item = Mockito.mock( UploadItem.class );
        when( item.getId() ).thenReturn( id );
        when( item.getMimeType() ).thenReturn( "image/png" );
        when( item.getUploadTime() ).thenReturn( 0L );
        when( item.getName() ).thenReturn( "image.png" );
        when( item.getSize() ).thenReturn( (long) data.length );

        when( this.service.getItem( id ) ).thenReturn( item );

        Response response = this.resource.getUploadedContent( "b8328bb4-6c57-475a-8697-96de97a1bf39" );
        assertEquals( 200, response.getStatus() );
    }

    private void addFile( final String id, final String name, final byte[] data, final String type )
        throws Exception
    {
        final InputStream in = new ByteArrayInputStream( data );

        final BodyPartEntity bodyPartEntity = Mockito.mock( BodyPartEntity.class );
        when( bodyPartEntity.getInputStream() ).thenReturn( in );
        final FormDataBodyPart part = Mockito.mock( FormDataBodyPart.class );
        when( part.getName() ).thenReturn( "file" );
        final FormDataContentDisposition contentDisposition = Mockito.mock( FormDataContentDisposition.class );
        when( contentDisposition.getFileName() ).thenReturn( name );
        when( part.getContentDisposition() ).thenReturn( contentDisposition );
        when( part.getMediaType() ).thenReturn( MediaType.valueOf( type ) );
        when( part.getValueAs( InputStream.class ) ).thenReturn( in );
        this.parts.bodyPart( part );

        final UploadItem item = Mockito.mock( UploadItem.class );
        when( item.getId() ).thenReturn( id );
        when( item.getMimeType() ).thenReturn( type );
        when( item.getUploadTime() ).thenReturn( 0L );
        when( item.getName() ).thenReturn( name );
        when( item.getSize() ).thenReturn( (long) data.length );

        when( this.service.upload( name, type, in ) ).thenReturn( item );
    }
}
