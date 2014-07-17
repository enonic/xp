package com.enonic.wem.core.version;

import com.enonic.wem.api.blob.BlobKey;

public interface VersionService
{
    public void store( final VersionDocument versionDocument );

    public VersionEntry getVersion( final BlobKey blobKey );

}
