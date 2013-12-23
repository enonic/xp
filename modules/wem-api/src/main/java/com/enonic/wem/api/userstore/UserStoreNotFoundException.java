package com.enonic.wem.api.userstore;

import com.enonic.wem.api.NotFoundException;

public final class UserStoreNotFoundException
    extends NotFoundException
{
    public UserStoreNotFoundException( final UserStoreName name )
    {
        super( "Userstore [{0}] was not found", name.toString() );
    }
}
