package com.enonic.wem.api.content.data;


import com.enonic.wem.api.blob.BlobKey;

public interface BlobKeyResolver
{
    BlobKey resolve( byte[] bytes );
}
