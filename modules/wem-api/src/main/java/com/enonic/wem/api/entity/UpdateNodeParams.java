package com.enonic.wem.api.entity;

import com.google.common.base.Preconditions;

public class UpdateNodeParams
{
    private EntityId id;

    private NodeEditor editor;

    private Workspace workspace;

    public UpdateNodeParams id( final EntityId value, final Workspace workspace )
    {
        this.id = value;
        this.workspace = workspace;
        return this;
    }

    public UpdateNodeParams editor( final NodeEditor value )
    {
        this.editor = value;
        return this;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.id, "id cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
        Preconditions.checkNotNull( this.workspace, "workspace cannot be null" );
    }

    public EntityId getId()
    {
        return id;
    }

    public NodeEditor getEditor()
    {
        return editor;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }
}
