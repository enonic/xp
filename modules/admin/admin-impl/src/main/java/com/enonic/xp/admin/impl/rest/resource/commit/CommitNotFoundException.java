package com.enonic.xp.admin.impl.rest.resource.commit;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.BaseException;
import com.enonic.xp.node.NodeCommitId;

@Beta
public class CommitNotFoundException
    extends BaseException
{
     public CommitNotFoundException( final NodeCommitId nodeCommitId )
    {
        super( "Commit [{0}] not found", nodeCommitId );
    }

}
