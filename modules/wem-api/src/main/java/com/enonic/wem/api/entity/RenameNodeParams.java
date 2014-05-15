package com.enonic.wem.api.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentConstants;

public class RenameNodeParams
{
    private EntityId entityId;

    private NodeName nodeName;

    private Workspace workspace = ContentConstants.DEFAULT_WORKSPACE;

    public RenameNodeParams entityId( final EntityId entityId )
    {
        this.entityId = entityId;
        return this;
    }

    public RenameNodeParams nodeName( final NodeName nodeName )
    {
        this.nodeName = nodeName;
        return this;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.entityId, "id cannot be null" );
        Preconditions.checkNotNull( this.nodeName, "name cannot be null" );
    }

    public EntityId getEntityId()
    {
        return entityId;
    }

    public NodeName getNodeName()
    {
        return nodeName;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }
}
