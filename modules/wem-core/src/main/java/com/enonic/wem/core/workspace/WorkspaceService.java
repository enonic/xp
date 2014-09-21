package com.enonic.wem.core.workspace;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.NodeVersionIds;
import com.enonic.wem.core.workspace.compare.query.CompareWorkspacesQuery;

public interface WorkspaceService
{
    public void store( final StoreWorkspaceDocument storeWorkspaceDocument, final WorkspaceContext context );

    public void delete( final EntityId entityId, final WorkspaceContext context );

    public NodeVersionId getCurrentVersion( final EntityId entityId, final WorkspaceContext context );

    public NodeVersionIds getByVersionIds( final EntityIds entityIds, final WorkspaceContext context );

    public NodeVersionId getByPath( final NodePath nodePath, final WorkspaceContext context );

    public NodeVersionIds getByPaths( final NodePaths nodePaths, final WorkspaceContext context );

    public NodeVersionIds findByParent( final NodePath parentPath, final WorkspaceContext context );

    public EntityIds findNodesWithDifferences( final CompareWorkspacesQuery query, final WorkspaceContext context );

    public boolean hasChildren( final NodePath parentPath, final WorkspaceContext context );

}
