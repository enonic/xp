package com.enonic.wem.core.content.data;


import org.junit.Test;

import com.enonic.cms.framework.blob.BlobKey;

import static org.junit.Assert.*;

public class MockBlobKeyResolverTest
{
    @Test
    public void given_byte_array_when_resolve_then_blob_key_is_returned()
    {
        MockBlobKeyResolver resolver = new MockBlobKeyResolver();
        BlobKey blobKey = resolver.resolve( new byte[]{1, 2, 3} );
        assertNotNull( blobKey );
    }
}
