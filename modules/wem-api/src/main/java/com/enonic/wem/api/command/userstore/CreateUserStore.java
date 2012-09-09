package com.enonic.wem.api.command.userstore;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;

public final class CreateUserStore
    extends Command<UserStoreName>
{
    private UserStore userStore;

    public UserStore getUserStore()
    {
        return this.userStore;
    }

    public CreateUserStore userStore( final UserStore userStore )
    {
        this.userStore = userStore;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.userStore, "UserStore cannot be null" );
    }
}
