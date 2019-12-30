package com.enonic.xp.exception;

import java.text.MessageFormat;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.User;

@PublicApi
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
