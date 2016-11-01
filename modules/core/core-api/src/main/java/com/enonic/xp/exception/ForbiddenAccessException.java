package com.enonic.xp.exception;

import java.text.MessageFormat;

import com.google.common.annotations.Beta;

import com.enonic.xp.security.User;

@Beta
public final class ForbiddenAccessException
    extends BaseException
{
    private final User user;

    public ForbiddenAccessException( final User user )
    {
        super( MessageFormat.format( "Access denied to user [{0}] {1}", user == null ? "unknown" : user.getKey(),
                                     user != null && user.getDisplayName() != null ? "''" + user.getDisplayName() + "''" : "" ) );
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }
}
