package com.enonic.xp.security.auth;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;

import com.enonic.xp.security.UserStoreKey;

@Beta
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
        if ( StringUtils.countMatches( username, "\\" ) == 1 )
        {
            final String[] userParts = username.split( "\\\\" );
            if ( userParts.length != 2 )
            {
                this.username = username;
                return;
            }

            try
            {
                setUserStore( UserStoreKey.from( userParts[0] ) );
                this.username = userParts[1];
            }
            catch ( java.lang.IllegalArgumentException e )
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
