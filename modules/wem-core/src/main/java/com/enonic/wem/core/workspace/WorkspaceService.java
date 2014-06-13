package com.enonic.wem.core.workspace;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobKeys;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.core.workspace.diff.query.WorkspacesDiffQuery;
import com.enonic.wem.core.workspace.query.WorkspaceDeleteQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdsQuery;
import com.enonic.wem.core.workspace.query.WorkspaceParentQuery;
import com.enonic.wem.core.workspace.query.WorkspacePathQuery;
import com.enonic.wem.core.workspace.query.WorkspacePathsQuery;

public interface WorkspaceService
{
    public void store( final WorkspaceDocument workspaceDocument );

    public void delete( final WorkspaceDeleteQuery query );

    public BlobKey getById( final WorkspaceIdQuery query );

    public BlobKeys getByIds( final WorkspaceIdsQuery query );

    public BlobKey getByPath( final WorkspacePathQuery query );

    public BlobKeys getByPaths( final WorkspacePathsQuery query );

    public BlobKeys getByParent( final WorkspaceParentQuery query );

    public EntityIds getEntriesWithDiff( final WorkspacesDiffQuery query );

}
