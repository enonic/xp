package com.enonic.wem.api.item;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Command;


public class UpdateItem
    extends Command<UpdateItemResult>
{
    private UserKey modifier;

    private ItemId itemId;

    private ItemEditor editor;

    public UpdateItem modifier( final UserKey value )
    {
        this.modifier = value;
        return this;
    }

    public UpdateItem item( final ItemId value )
    {
        this.itemId = value;
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
        Preconditions.checkNotNull( this.modifier, "modifier cannot be null" );
        Preconditions.checkNotNull( this.itemId, "itemId cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public ItemId getItemId()
    {
        return itemId;
    }

    public ItemEditor getEditor()
    {
        return editor;
    }
}
