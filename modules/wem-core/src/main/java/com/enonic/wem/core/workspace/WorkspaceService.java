package com.enonic.wem.core.workspace;

import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.NodeVersionIds;
import com.enonic.wem.core.workspace.compare.query.CompareWorkspacesQuery;
import com.enonic.wem.core.workspace.query.WorkspaceDeleteQuery;
import com.enonic.wem.core.workspace.query.WorkspaceHasChildrenQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdsQuery;
import com.enonic.wem.core.workspace.query.WorkspaceParentQuery;
import com.enonic.wem.core.workspace.query.WorkspacePathQuery;
import com.enonic.wem.core.workspace.query.WorkspacePathsQuery;

public interface WorkspaceService
{
    public void store( final StoreWorkspaceDocument storeWorkspaceDocument );

    public void delete( final WorkspaceDeleteQuery query );

    public NodeVersionId getCurrentVersion( final WorkspaceIdQuery query );

    public NodeVersionIds getByVersionIds( final WorkspaceIdsQuery query );

    public NodeVersionId getByPath( final WorkspacePathQuery query );

    public NodeVersionIds getByPaths( final WorkspacePathsQuery query );

    public NodeVersionIds findByParent( final WorkspaceParentQuery query );

    public EntityIds getEntriesWithDiff( final CompareWorkspacesQuery query );

    public boolean hasChildren( final WorkspaceHasChildrenQuery query );

}
