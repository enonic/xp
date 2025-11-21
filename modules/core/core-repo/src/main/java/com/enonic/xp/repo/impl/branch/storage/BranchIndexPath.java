package com.enonic.xp.repo.impl.branch.storage;

import com.enonic.xp.index.IndexPath;

public class BranchIndexPath
{
    public static final IndexPath TIMESTAMP = IndexPath.from( "timestamp" );

    public static final IndexPath VERSION_ID = IndexPath.from( "versionId" );

    public static final IndexPath NODE_BLOB_KEY = IndexPath.from( "nodeBlobKey" );

    public static final IndexPath INDEX_CONFIG_BLOB_KEY = IndexPath.from( "indexConfigBlobKey" );

    public static final IndexPath ACCESS_CONTROL_BLOB_KEY = IndexPath.from( "accessControlBlobKey" );

    public static final IndexPath BRANCH_NAME = IndexPath.from( "branch" );

    public static final IndexPath NODE_ID = IndexPath.from( "nodeId" );

    public static final IndexPath STATE = IndexPath.from( "state" );

    public static final IndexPath PATH = IndexPath.from( "path" );

    public static IndexPath[] entryFields()
    {
        return new IndexPath[]{PATH, VERSION_ID, NODE_BLOB_KEY, INDEX_CONFIG_BLOB_KEY, ACCESS_CONTROL_BLOB_KEY, TIMESTAMP, NODE_ID};
    }
}
