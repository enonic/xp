package com.enonic.wem.api.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;


public class UpdateNode
    extends Command<UpdateItemResult>
{
    private EntityId item;

    private NodeEditor editor;

    public UpdateNode item( final EntityId value )
    {
        this.item = value;
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
        Preconditions.checkNotNull( this.item, "item cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }

    public EntityId getItem()
    {
        return item;
    }

    public NodeEditor getEditor()
    {
        return editor;
    }
}
