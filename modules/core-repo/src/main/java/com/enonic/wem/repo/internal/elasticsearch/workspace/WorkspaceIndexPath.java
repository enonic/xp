package com.enonic.wem.repo.internal.elasticsearch.workspace;

import com.enonic.wem.api.index.IndexPath;

public class WorkspaceIndexPath
{
    public static final IndexPath VERSION_ID = IndexPath.from( "versionId" );

    public static final IndexPath WORKSPACE_ID = IndexPath.from( "workspace" );

    public static final IndexPath NODE_ID = IndexPath.from( "nodeId" );

    public static final IndexPath STATE = IndexPath.from( "state" );

    public static final IndexPath PATH = IndexPath.from( "path" );
}
