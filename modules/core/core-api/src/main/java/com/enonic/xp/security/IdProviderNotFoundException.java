package com.enonic.xp.security;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.BaseException;

@Beta
public class IdProviderNotFoundException
    extends BaseException
{

    private final IdProviderKey idProviderKey;

    public IdProviderNotFoundException( final IdProviderKey idProviderKey )
    {
        super( "IdProvider [{0}] not found", idProviderKey );
        this.idProviderKey = idProviderKey;
    }

    public IdProviderKey getIdProviderKey()
    {
        return idProviderKey;
    }
}
