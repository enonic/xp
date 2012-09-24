package com.enonic.wem.api.content.data;


import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobKeyCreator;

public class MockBlobKeyResolver
    implements BlobKeyResolver
{
    @Override
    public BlobKey resolve( final byte[] bytes )
    {
        return BlobKeyCreator.createKey( bytes );
    }
}
