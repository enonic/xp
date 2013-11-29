package com.enonic.wem.admin.rest.resource.upload;

import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.admin.rest.service.upload.UploadItem;
import com.enonic.wem.admin.rest.service.upload.UploadService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class UploadResourceTest
    extends AbstractResourceTest
{
    private FormDataMultiPart multiPart;

    private UploadService service;

    @Override
    protected Object getResourceInstance()
    {
        this.multiPart = new FormDataMultiPart();
        this.service = Mockito.mock( UploadService.class );

        final UploadResource resource = new UploadResource();
        resource.setUploadService( this.service );

        return resource;
    }

    @Test
    public void testUpload_onePart()
        throws Exception
    {
        addFile( "1", "text.txt", new byte[10], "text/plain" );
        final String json = resource().path( "/upload" ).type( MediaType.MULTIPART_FORM_DATA_TYPE ).post( String.class, this.multiPart );
        assertJson( "upload_one_part.json", json );
    }

    @Test
    public void testUpload_twoParts()
        throws Exception
    {
        addFile( "1", "text.txt", new byte[10], "text/plain" );
        addFile( "2", "image.png", new byte[20], "image/png" );
        final String json = resource().path( "/upload" ).type( MediaType.MULTIPART_FORM_DATA_TYPE ).post( String.class, this.multiPart );
        assertJson( "upload_two_parts.json", json );
    }

    @Test
    public void testGetUploadedContentNotFound()
        throws Exception
    {
        final ClientResponse response =
            resource().path( "/upload" ).path( "b8328bb4-6c57-475a-8697-96de97a1bf39" ).get( ClientResponse.class );
        assertEquals( 404, response.getStatus() );
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

        final ClientResponse response =
            resource().path( "/upload" ).path( "b8328bb4-6c57-475a-8697-96de97a1bf39" ).get( ClientResponse.class );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getType().toString() );
    }

    private void addFile( final String id, final String name, final byte[] data, final String type )
        throws Exception
    {
        final FormDataBodyPart part = new FormDataBodyPart( "file", data, MediaType.valueOf( type ) );
        final FormDataContentDisposition.FormDataContentDispositionBuilder dispositionBuilder = FormDataContentDisposition.name( "file" );
        dispositionBuilder.fileName( name );
        part.setContentDisposition( dispositionBuilder.build() );

        this.multiPart.bodyPart( part );

        final UploadItem item = Mockito.mock( UploadItem.class );
        when( item.getId() ).thenReturn( id );
        when( item.getMimeType() ).thenReturn( type );
        when( item.getUploadTime() ).thenReturn( 0L );
        when( item.getName() ).thenReturn( name );
        when( item.getSize() ).thenReturn( (long) data.length );

        when( this.service.upload( Mockito.eq( name ), Mockito.eq( type ), Mockito.any( InputStream.class ) ) ).thenReturn( item );
    }
}
