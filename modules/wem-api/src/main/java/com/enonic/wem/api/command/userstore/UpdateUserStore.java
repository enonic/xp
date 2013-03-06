package com.enonic.wem.api.command.userstore;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.command.UpdateResult;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.editor.UserStoreEditor;

public final class UpdateUserStore
    extends Command<UpdateResult>
{
    private UserStoreName name;

    private UserStoreEditor editor;

    public UpdateUserStore name( final UserStoreName name )
    {
        this.name = name;
        return this;
    }

    public UpdateUserStore editor( final UserStoreEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public UserStoreName getName()
    {
        return this.name;
    }

    public UserStoreEditor getEditor()
    {
        return this.editor;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.name, "UserStore name cannot be null" );
        Preconditions.checkNotNull( this.editor, "UserStore editor cannot be null" );
    }
}
