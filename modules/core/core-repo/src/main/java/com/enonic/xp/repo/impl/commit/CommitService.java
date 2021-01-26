package com.enonic.xp.repo.impl.commit;

import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.repo.impl.InternalContext;

public interface CommitService
{
    String store( NodeCommitEntry nodeBranchEntry, InternalContext context );

    NodeCommitEntry get( NodeCommitId nodeCommitId, InternalContext context );
}
