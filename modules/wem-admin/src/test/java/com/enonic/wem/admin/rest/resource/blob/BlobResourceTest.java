package com.enonic.wem.admin.rest.resource.blob;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;

public class BlobResourceTest
    extends AbstractResourceTest
{
    private BlobService blobService;

    @Override
    protected Object getResourceInstance()
    {
        this.blobService = Mockito.mock( BlobService.class );
        final BlobResource resource = new BlobResource();
        resource.setBlobService( this.blobService );
        return resource;
    }

    @Test
    public void test_upload()
        throws Exception
    {
        final Blob blob = Mockito.mock( Blob.class );
        Mockito.when( blob.getKey() ).thenReturn( new BlobKey( "123" ) );
        Mockito.when( blob.getLength() ).thenReturn( 10L );
        Mockito.when( this.blobService.create( Mockito.any() ) ).thenReturn( blob );

        final String jsonString = request().
            path( "blob/upload" ).
            multipart( "file", "test.txt", new byte[0], MediaType.TEXT_PLAIN_TYPE ).
            post().getAsString();

        Assert.assertTrue( jsonString.contains( "\"id\":\"123\"" ) );
        Assert.assertTrue( jsonString.contains( "\"size\":10" ) );
    }
}
