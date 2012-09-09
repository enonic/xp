package com.enonic.wem.api.command.userstore;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.userstore.UserStoreNames;

public final class DeleteUserStores
    extends Command<Integer>
{
    private UserStoreNames names;

    public DeleteUserStores names( final UserStoreNames names )
    {
        this.names = names;
        return this;
    }

    public UserStoreNames getNames()
    {
        return this.names;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.names, "UserStore names cannot be null" );
    }
}
