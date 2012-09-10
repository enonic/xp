package com.enonic.wem.core.content.data;

import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.BlobKeyCreator;


public class MockBlobKeyResolver
    implements BlobKeyResolver
{
    @Override
    public BlobKey resolve( final byte[] bytes )
    {
        return BlobKeyCreator.createKey( bytes );
    }
}
