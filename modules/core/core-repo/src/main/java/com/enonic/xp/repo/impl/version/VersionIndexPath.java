package com.enonic.xp.repo.impl.version;

import com.enonic.xp.index.IndexPath;

public class VersionIndexPath
{
    public static final IndexPath VERSION_ID = IndexPath.from( "versionid" );

    public static final IndexPath NODE_BLOB_KEY = IndexPath.from( "nodeblobkey" );

    public static final IndexPath INDEX_CONFIG_BLOB_KEY = IndexPath.from( "indexconfigblobkey" );

    public static final IndexPath ACCESS_CONTROL_BLOB_KEY = IndexPath.from( "accesscontrolblobkey" );

    public static final IndexPath BINARY_BLOB_KEYS = IndexPath.from( "binaryblobkeys" );

    public static final IndexPath NODE_ID = IndexPath.from( "nodeid" );

    public static final IndexPath TIMESTAMP = IndexPath.from( "timestamp" );

    public static final IndexPath NODE_PATH = IndexPath.from( "nodepath" );

    public static final IndexPath COMMIT_ID = IndexPath.from( "commitid" );

    public static final IndexPath ATTRIBUTES = IndexPath.from( "attributes" );

    public static IndexPath[] entryFields()
    {
        return new IndexPath[]{VERSION_ID, NODE_BLOB_KEY, INDEX_CONFIG_BLOB_KEY, ACCESS_CONTROL_BLOB_KEY, BINARY_BLOB_KEYS, NODE_ID,
            TIMESTAMP, NODE_PATH, COMMIT_ID, ATTRIBUTES};
    }
}
