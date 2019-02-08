package com.enonic.xp.repo.impl.commit.storage;

import java.time.Instant;

import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.repo.impl.ReturnValues;

public class NodeCommitEntryFactory
{
    public static NodeCommitEntry create( final ReturnValues returnValues )
    {

        final Object commitId = returnValues.getSingleValue( CommitIndexPath.COMMIT_ID.getPath() );
        final Object message = returnValues.getSingleValue( CommitIndexPath.MESSAGE.getPath() );
        final Object timestamp = returnValues.getSingleValue( CommitIndexPath.TIMESTAMP.getPath() );
        final Object committer = returnValues.getSingleValue( CommitIndexPath.COMMITTER.getPath() );

        return NodeCommitEntry.create().
            nodeCommitId( NodeCommitId.from( commitId.toString() ) ).
            message( message.toString() ).
            timestamp( Instant.parse( timestamp.toString() ) ).
            committer( committer.toString() ).
            build();
    }
}
