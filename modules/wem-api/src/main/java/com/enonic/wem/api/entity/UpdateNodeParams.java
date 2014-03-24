package com.enonic.wem.api.entity;

import com.google.common.base.Preconditions;

public class UpdateNodeParams
{
    private EntityId id;

    private NodeEditor editor;

    public UpdateNodeParams id( final EntityId value )
    {
        this.id = value;
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
    }

    public EntityId getId()
    {
        return id;
    }

    public NodeEditor getEditor()
    {
        return editor;
    }
}
