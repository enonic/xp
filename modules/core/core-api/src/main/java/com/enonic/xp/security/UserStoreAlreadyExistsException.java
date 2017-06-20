package com.enonic.xp.security;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.BaseException;

@Beta
public class UserStoreAlreadyExistsException
    extends BaseException
{
    private final UserStoreKey userStoreKey;

    public UserStoreAlreadyExistsException( final UserStoreKey userStoreKey )
    {
        super( "User Store [{0}] could not be created. A User Store with that name already exists", userStoreKey );
        this.userStoreKey = userStoreKey;
    }

    public UserStoreKey getUserStoreKey()
    {
        return userStoreKey;
    }
}
