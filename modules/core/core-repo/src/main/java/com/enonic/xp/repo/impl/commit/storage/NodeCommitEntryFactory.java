package com.enonic.xp.repo.impl.commit.storage;

import java.time.Instant;

import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.storage.spi.CommitRecord;

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

    /** Adapts a domain {@link NodeCommitEntry} onto the SPI {@link CommitRecord}, for {@code NodeStore} store/get results. */
    public static CommitRecord toRecord( final NodeCommitEntry entry )
    {
        return new CommitRecord( entry.getNodeCommitId().toString(), entry.getMessage(), entry.getCommitter().toString(),
                                  entry.getTimestamp() );
    }

    /** Inverse of {@link #toRecord}, for consumers of {@code NodeStore} rebuilding the domain object. */
    public static NodeCommitEntry fromRecord( final CommitRecord record )
    {
        return NodeCommitEntry.create()
            .nodeCommitId( NodeCommitId.from( record.commitId() ) )
            .message( record.message() )
            .committer( record.committer() == null ? null : PrincipalKey.from( record.committer() ) )
            .timestamp( record.timestamp() )
            .build();
    }
}
