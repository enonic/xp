package com.enonic.xp.security;

import java.text.MessageFormat;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.NotFoundException;

@PublicApi
public class PrincipalNotFoundException
    extends NotFoundException
{
    private final PrincipalKey principalKey;

    public PrincipalNotFoundException( final PrincipalKey principalKey )
    {
        super( MessageFormat.format( "Principal [{0}] not found", principalKey ) );
        this.principalKey = principalKey;
    }

    public PrincipalKey getPrincipal()
    {
        return principalKey;
    }
}
