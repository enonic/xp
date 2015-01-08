package com.enonic.wem.repo.internal.workspace;

import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.WorkspaceDiffResult;
import com.enonic.wem.repo.internal.workspace.compare.query.WorkspaceDiffQuery;

public interface WorkspaceService
{
    public void store( final StoreWorkspaceDocument storeWorkspaceDocument, final WorkspaceContext context );

    public void delete( final NodeId nodeId, final WorkspaceContext context );

    public WorkspaceDiffResult diff( final WorkspaceDiffQuery query, final WorkspaceContext context );
}
