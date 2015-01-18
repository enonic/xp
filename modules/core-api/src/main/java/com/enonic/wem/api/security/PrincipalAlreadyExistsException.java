package com.enonic.wem.api.security;

import com.enonic.wem.api.exception.BaseException;

public class PrincipalAlreadyExistsException
    extends BaseException
{

    private final PrincipalKey principalKey;

    public PrincipalAlreadyExistsException( final PrincipalKey principalKey )
    {
        super( "Principal [{0}] already exists", principalKey );
        this.principalKey = principalKey;
    }

    public PrincipalKey getPrincipal()
    {
        return principalKey;
    }
}
