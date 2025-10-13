package com.enonic.xp.repo.impl.commit.storage;

import com.enonic.xp.index.IndexPath;

public class CommitIndexPath
{
    public static final IndexPath COMMIT_ID = IndexPath.from( "commitId" );

    public static final IndexPath MESSAGE = IndexPath.from( "message" );

    public static final IndexPath TIMESTAMP = IndexPath.from( "timestamp" );

    public static final IndexPath COMMITTER = IndexPath.from( "committer" );

    public static IndexPath[] entryFields()
    {
        return new IndexPath[]{COMMIT_ID, MESSAGE, TIMESTAMP, COMMITTER};
    }
}
