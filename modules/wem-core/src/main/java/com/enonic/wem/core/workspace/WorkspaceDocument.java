package com.enonic.wem.core.workspace;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Workspace;

public class WorkspaceDocument
{
    private final EntityId entityId;

    private final BlobKey blobKey;

    private final NodePath path;

    private final NodePath parentPath;

    private final Workspace workspace;

    private WorkspaceDocument( final Builder builder )
    {
        this.entityId = builder.entityId;
        this.blobKey = builder.blobKey;
        this.parentPath = builder.parentPath;
        this.path = builder.path;
        this.workspace = builder.workspace;
    }

    public EntityId getEntityId()
    {
        return entityId;
    }

    public BlobKey getBlobKey()
    {
        return blobKey;
    }

    public NodePath getPath()
    {
        return path;
    }

    public NodePath getParentPath()
    {
        return parentPath;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static class Builder
    {
        private EntityId entityId;

        private BlobKey blobKey;

        private NodePath path;

        private NodePath parentPath;

        private Workspace workspace;


        public Builder node( final Node node )
        {
            this.path = node.path();
            this.parentPath = node.parent();
            this.entityId = node.id();

            return this;
        }

        public Builder workspace( final Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public Builder blobKey( final BlobKey blobKey )
        {
            this.blobKey = blobKey;
            return this;
        }


        public WorkspaceDocument build()
        {
            return new WorkspaceDocument( this );
        }


    }

}
