package com.enonic.wem.repo.internal.workspace;

import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.repo.internal.index.query.NodeWorkspaceVersion;

public interface WorkspaceService
{
    public void store( final StoreWorkspaceDocument storeWorkspaceDocument, final WorkspaceContext context );

    public void delete( final NodeId nodeId, final WorkspaceContext context );

    public NodeWorkspaceVersion get( final NodeId nodeId, final WorkspaceContext context );

    public NodeWorkspaceVersion get( final NodePath nodePath, final WorkspaceContext context );
}
