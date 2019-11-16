package com.enonic.xp.security.auth;

import com.google.common.annotations.Beta;

import com.enonic.xp.security.IdProviderKey;

@Beta
public final class VerifiedUsernameAuthToken
    extends AuthenticationToken
{
    private String username;

    public String getUsername()
    {
        return this.username;
    }

    public void setUsername( final String username )
    {
        final String[] userParts = username.split( "\\\\" );
        if ( userParts.length != 2 )
        {
            this.username = username;
            return;
        }

        try
        {
            setIdProvider( IdProviderKey.from( userParts[0] ) );
            this.username = userParts[1];
        }
        catch ( IllegalArgumentException e )
        {
            this.username = username;
        }
    }
}
