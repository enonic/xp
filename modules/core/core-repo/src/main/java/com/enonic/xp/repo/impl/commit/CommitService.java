package com.enonic.xp.repo.impl.commit;

import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.repo.impl.InternalContext;

public interface CommitService
{
    String store( final NodeBranchEntry nodeBranchEntry, final InternalContext context );
}
