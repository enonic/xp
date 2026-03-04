package com.enonic.xp.security;

import java.text.MessageFormat;

import com.enonic.xp.exception.NotFoundException;


public class IdProviderNotFoundException
    extends NotFoundException
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
