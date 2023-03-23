package com.enonic.xp.security;

import java.text.MessageFormat;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.BaseException;

@PublicApi
public class PrincipalAlreadyExistsException
    extends BaseException
{

    private final PrincipalKey principalKey;

    public PrincipalAlreadyExistsException( final PrincipalKey principalKey )
    {
        super( MessageFormat.format( "Principal [{0}] could not be created. A principal with that name already exists", principalKey ) );
        this.principalKey = principalKey;
    }

    public PrincipalKey getPrincipal()
    {
        return principalKey;
    }
}
