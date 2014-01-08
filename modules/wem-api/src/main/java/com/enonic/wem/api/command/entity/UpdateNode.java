package com.enonic.wem.api.command.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NodeEditor;


public class UpdateNode
    extends Command<UpdateNodeResult>
{
    private EntityId id;

    private NodeEditor editor;

    public UpdateNode id( final EntityId value )
    {
        this.id = value;
        return this;
    }

    public UpdateNode editor( final NodeEditor value )
    {
        this.editor = value;
        return this;
    }

    @Override
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
