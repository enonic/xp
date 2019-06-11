package com.enonic.xp.commit;

import com.enonic.xp.context.InternalContext;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;

public interface CommitService
{
    String store( final NodeCommitEntry nodeBranchEntry, final InternalContext context );

    NodeCommitEntry get( final NodeCommitId nodeCommitId, final InternalContext context );
}
