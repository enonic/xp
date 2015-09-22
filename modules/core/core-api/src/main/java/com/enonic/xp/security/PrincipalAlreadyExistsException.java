package com.enonic.xp.security;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.BaseException;

@Beta
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
