package com.enonic.wem.api.content.data;


import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.blob.BlobKey;

public class MockBlobKeyResolverTest
{
    @Test
    public void given_byte_array_when_resolve_then_blob_key_is_returned()
    {
        MockBlobKeyResolver resolver = new MockBlobKeyResolver();
        BlobKey blobKey = resolver.resolve( new byte[]{1, 2, 3} );
        Assert.assertNotNull( blobKey );
    }
}
