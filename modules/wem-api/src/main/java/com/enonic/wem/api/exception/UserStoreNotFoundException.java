package com.enonic.wem.api.exception;

import com.enonic.wem.api.userstore.UserStoreName;

public final class UserStoreNotFoundException
    extends BaseException
{
    public UserStoreNotFoundException( final UserStoreName name )
    {
        super( "Userstore [{0}] was not found", name.toString() );
    }
}
