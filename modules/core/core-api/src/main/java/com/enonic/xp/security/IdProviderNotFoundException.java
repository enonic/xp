package com.enonic.xp.security;

import java.text.MessageFormat;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.BaseException;

@PublicApi
public class IdProviderNotFoundException
    extends BaseException
{

    private final IdProviderKey idProviderKey;

    public IdProviderNotFoundException( final IdProviderKey idProviderKey )
    {
        super( MessageFormat.format( "IdProvider [{0}] not found", idProviderKey ) );
        this.idProviderKey = idProviderKey;
    }

    public IdProviderKey getIdProviderKey()
    {
        return idProviderKey;
    }
}
