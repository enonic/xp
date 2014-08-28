package com.enonic.wem.core.version;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.EntityVersion;
import com.enonic.wem.api.entity.FindEntityVersionsResult;

public interface VersionService
{
    public void store( final EntityVersionDocument entityVersionDocument );

    public EntityVersion getVersion( final BlobKey blobKey );

    public FindEntityVersionsResult findVersions( final GetVersionsQuery query );

}
