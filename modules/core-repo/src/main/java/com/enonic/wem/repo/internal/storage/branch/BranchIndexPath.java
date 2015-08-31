package com.enonic.wem.repo.internal.storage.branch;

import com.enonic.xp.index.IndexPath;

public class BranchIndexPath
{
    public static final IndexPath TIMESTAMP = IndexPath.from( "timestamp" );

    public static final IndexPath VERSION_ID = IndexPath.from( "versionId" );

    public static final IndexPath BRANCH_NAME = IndexPath.from( "branch" );

    public static final IndexPath NODE_ID = IndexPath.from( "nodeId" );

    public static final IndexPath STATE = IndexPath.from( "state" );

    public static final IndexPath PATH = IndexPath.from( "path" );

    public static final IndexPath READ_ACCESS_LIST = IndexPath.from( "permissions.read" );
}
