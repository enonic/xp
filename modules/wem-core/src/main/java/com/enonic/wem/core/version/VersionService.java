package com.enonic.wem.core.version;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.FindNodeVersionsResult;
import com.enonic.wem.api.entity.NodeVersion;

public interface VersionService
{
    public void store( final EntityVersionDocument entityVersionDocument );

    public NodeVersion getVersion( final BlobKey blobKey );

    public FindNodeVersionsResult findVersions( final GetVersionsQuery query );

}
