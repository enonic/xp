package com.enonic.xp.security;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.BaseException;

@Beta
public class PathGuardAlreadyExistsException
    extends BaseException
{

    private final String key;

    public PathGuardAlreadyExistsException( final String key )
    {
        super( "Path guard [{0}] already exists", key );
        this.key = key;
    }
}
