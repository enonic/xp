package com.enonic.wem.core.workspace;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.NodeVersionIds;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.workspace.compare.query.CompareWorkspacesQuery;

public interface WorkspaceService
{
    public void store( final StoreWorkspaceDocument storeWorkspaceDocument, final Context context );

    public void delete( final EntityId entityId, final Context context );

    public NodeVersionId getCurrentVersion( final EntityId entityId, final Context context );

    public NodeVersionId getWorkspaceVersion( final EntityId entityId, final Workspace workspace, final Context context );

    public NodeVersionIds getByVersionIds( final EntityIds entityIds, final Context context );

    public NodeVersionId getByPath( final NodePath nodePath, final Context context );

    public NodeVersionIds getByPaths( final NodePaths nodePaths, final Context context );

    public NodeVersionIds findByParent( final NodePath parentPath, final Context context );

    public EntityIds findNodesWithDifferences( final CompareWorkspacesQuery query, final Context context );

    public boolean hasChildren( final NodePath parentPath, final Context context );

}
