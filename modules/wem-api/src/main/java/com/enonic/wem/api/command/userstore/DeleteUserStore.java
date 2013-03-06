package com.enonic.wem.api.command.userstore;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.userstore.UserStoreName;

public final class DeleteUserStore
    extends Command<Boolean>
{
    private UserStoreName name;

    public DeleteUserStore name( final UserStoreName name )
    {
        this.name = name;
        return this;
    }

    public UserStoreName getName()
    {
        return this.name;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.name, "UserStore name cannot be null" );
    }
}
