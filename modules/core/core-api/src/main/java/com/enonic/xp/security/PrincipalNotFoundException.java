package com.enonic.xp.security;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.BaseException;

@PublicApi
public class PrincipalNotFoundException
    extends BaseException
{

    private final PrincipalKey principalKey;

    public PrincipalNotFoundException( final PrincipalKey principalKey )
    {
        super( "Principal [{0}] not found", principalKey );
        this.principalKey = principalKey;
    }

    public PrincipalKey getPrincipal()
    {
        return principalKey;
    }
}
