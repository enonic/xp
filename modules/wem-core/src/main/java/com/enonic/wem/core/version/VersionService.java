package com.enonic.wem.core.version;

import com.enonic.wem.api.entity.FindNodeVersionsResult;
import com.enonic.wem.api.entity.NodeVersion;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.repository.Repository;

public interface VersionService
{
    public void store( final EntityVersionDocument entityVersionDocument, final Repository repository );

    public NodeVersion getVersion( final NodeVersionId versionId, final Repository repository );

    public FindNodeVersionsResult findVersions( final GetVersionsQuery query, final Repository repository );

}
