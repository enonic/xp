package com.enonic.wem.api.security;

import com.enonic.wem.api.exception.BaseException;

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
