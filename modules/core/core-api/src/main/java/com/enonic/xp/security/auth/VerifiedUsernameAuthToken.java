package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.IdProviderKey;

@PublicApi
@NullMarked
public final class VerifiedUsernameAuthToken
    extends AuthenticationToken
{
    private final String username;

    public VerifiedUsernameAuthToken( final IdProviderKey idProvider, final String username )
    {
        super( idProvider );
        this.username = username;
    }

    public String getUsername()
    {
        return this.username;
    }
}
