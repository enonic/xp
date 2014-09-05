package com.enonic.wem.core.version;

import com.enonic.wem.api.entity.FindNodeVersionsResult;
import com.enonic.wem.api.entity.NodeVersion;
import com.enonic.wem.api.entity.NodeVersionId;

public interface VersionService
{
    public void store( final EntityVersionDocument entityVersionDocument );

    public NodeVersion getVersion( final NodeVersionId versionId );

    public FindNodeVersionsResult findVersions( final GetVersionsQuery query );

}
