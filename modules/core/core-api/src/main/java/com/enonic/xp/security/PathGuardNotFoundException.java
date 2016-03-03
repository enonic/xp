package com.enonic.xp.security;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.BaseException;

@Beta
public class PathGuardNotFoundException
    extends BaseException
{
    public PathGuardNotFoundException( final PathGuardKey key )
    {
        super( "PathGuard [{0}] not found", key.toString() );
    }
}
