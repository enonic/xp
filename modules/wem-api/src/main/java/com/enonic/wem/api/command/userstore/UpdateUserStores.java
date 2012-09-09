package com.enonic.wem.api.command.userstore;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.editor.UserStoreEditor;

public final class UpdateUserStores
    extends Command<Integer>
{
    private UserStoreNames names;

    private UserStoreEditor editor;

    public UpdateUserStores names( final UserStoreNames names )
    {
        this.names = names;
        return this;
    }

    public UpdateUserStores editor( final UserStoreEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public UserStoreNames getNames()
    {
        return this.names;
    }

    public UserStoreEditor getEditor()
    {
        return this.editor;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.names, "UserStore names cannot be null" );
        Preconditions.checkNotNull( this.editor, "UserStore editor cannot be null" );
    }
}
