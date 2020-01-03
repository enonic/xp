package com.enonic.xp.security.auth;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.IdProviderKey;

@PublicApi
public final class UsernamePasswordAuthToken
    extends PasswordAuthToken
{
    private String username;

    public String getUsername()
    {
        return this.username;
    }

    public void setUsername( final String username )
    {
        if ( username.chars().filter( c -> c == '\\' ).count() == 1 )
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
        else
        {
            this.username = username;
        }
    }
}
