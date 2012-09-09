package com.enonic.wem.web.rest.resource.upload;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.sun.jersey.multipart.file.StreamDataBodyPart;

import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.AbstractResourceTest;
import com.enonic.wem.web.rest.service.upload.UploadItem;
import com.enonic.wem.web.rest.service.upload.UploadService;

public class UploadResourceTest
    extends AbstractResourceTest
{
    private UploadResource resource;

    private List<StreamDataBodyPart> parts;

    private UploadService service;

    @Before
    public void setUp()
    {
        this.parts = Lists.newArrayList();

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

    private void addFile( final String id, final String name, final byte[] data, final String type )
        throws Exception
    {
        final InputStream in = new ByteArrayInputStream( data );

        final StreamDataBodyPart part = new StreamDataBodyPart( "file", in );
        part.setFilename( name );
        part.setStreamEntity( in );
        part.setMediaType( MediaType.valueOf( type ) );
        this.parts.add( part );

        final UploadItem item = Mockito.mock( UploadItem.class );
        Mockito.when( item.getId() ).thenReturn( id );
        Mockito.when( item.getMimeType() ).thenReturn( type );
        Mockito.when( item.getUploadTime() ).thenReturn( 0L );
        Mockito.when( item.getName() ).thenReturn( name );
        Mockito.when( item.getSize() ).thenReturn( (long) data.length );

        Mockito.when( this.service.upload( name, type, in ) ).thenReturn( item );
    }
}
