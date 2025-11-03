package com.enonic.xp.repo.impl.commit.storage;

import java.time.Instant;

import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.security.PrincipalKey;

public class NodeCommitEntryFactory
{
    public static NodeCommitEntry create( final ReturnValues returnValues )
    {
        final NodeCommitId commitId = NodeCommitId.from( returnValues.getStringValue( CommitIndexPath.COMMIT_ID ) );
        final String message = returnValues.getStringValue( CommitIndexPath.MESSAGE );
        final Instant timestamp = Instant.parse( returnValues.getStringValue( CommitIndexPath.TIMESTAMP ) );
        final PrincipalKey committer = PrincipalKey.from( returnValues.getStringValue( CommitIndexPath.COMMITTER ) );

        return NodeCommitEntry.create().nodeCommitId( commitId ).message( message ).timestamp( timestamp ).committer( committer ).build();
    }
}
