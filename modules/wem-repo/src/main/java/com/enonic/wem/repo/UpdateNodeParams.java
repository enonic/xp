package com.enonic.wem.repo;

import com.google.common.base.Preconditions;

public class UpdateNodeParams
{
    private NodeId id;

    private NodeEditor editor;

    public UpdateNodeParams id( final NodeId value )
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

    public NodeId getId()
    {
        return id;
    }

    public NodeEditor getEditor()
    {
        return editor;
    }

}
