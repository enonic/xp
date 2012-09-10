package com.enonic.wem.core.content.data;


import com.enonic.cms.framework.blob.BlobKey;

public interface BlobKeyResolver
{
    BlobKey resolve( byte[] bytes );
}
