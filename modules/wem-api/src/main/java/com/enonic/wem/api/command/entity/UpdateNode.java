package com.enonic.wem.api.command.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NodeEditor;


public class UpdateNode
    extends Command<UpdateNodeResult>
{
    private EntityId node;

    private NodeEditor editor;

    public UpdateNode item( final EntityId value )
    {
        this.node = value;
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
        Preconditions.checkNotNull( this.node, "item cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }

    public EntityId getNode()
    {
        return node;
    }

    public NodeEditor getEditor()
    {
        return editor;
    }
}
