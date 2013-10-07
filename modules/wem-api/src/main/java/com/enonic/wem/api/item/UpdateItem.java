package com.enonic.wem.api.item;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;


public class UpdateItem
    extends Command<UpdateItemResult>
{
    private ItemId item;

    private ItemEditor editor;

    public UpdateItem item( final ItemId value )
    {
        this.item = value;
        return this;
    }

    public UpdateItem editor( final ItemEditor value )
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

    public ItemId getItem()
    {
        return item;
    }

    public ItemEditor getEditor()
    {
        return editor;
    }
}
