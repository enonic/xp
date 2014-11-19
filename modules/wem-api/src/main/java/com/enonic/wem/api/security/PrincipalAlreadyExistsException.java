package com.enonic.wem.api.security;

import com.enonic.wem.api.exception.BaseException;

public class PrincipalAlreadyExistsException
    extends BaseException
{

    public PrincipalAlreadyExistsException( final PrincipalKey principalKey )
    {
        super( "Principal [{0}] already exists", principalKey );
    }
}
