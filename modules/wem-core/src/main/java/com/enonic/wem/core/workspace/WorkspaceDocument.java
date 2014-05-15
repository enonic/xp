package com.enonic.wem.core.workspace;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Workspace;

public class WorkspaceDocument
{
    private EntityId entityId;

    private BlobKey blobKey;

    private NodePath path;

    private NodePath parentPath;

    private Workspace workspace;

    public EntityId getEntityId()
    {
        return entityId;
    }

    public void setEntityId( final EntityId entityId )
    {
        this.entityId = entityId;
    }

    public BlobKey getBlobKey()
    {
        return blobKey;
    }

    public void setBlobKey( final BlobKey blobKey )
    {
        this.blobKey = blobKey;
    }

    public NodePath getPath()
    {
        return path;
    }

    public void setPath( final NodePath path )
    {
        this.path = path;
    }

    public NodePath getParentPath()
    {
        return parentPath;
    }

    public void setParentPath( final NodePath parentPath )
    {
        this.parentPath = parentPath;
    }

    public String getWorkspaceName()
    {
        return workspace.getName();
    }

    public void setWorkspace( final Workspace workspace )
    {
        this.workspace = workspace;
    }
}
