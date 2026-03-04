package com.enonic.xp.security;

import java.text.MessageFormat;

import com.enonic.xp.exception.DuplicateElementException;


public class IdProviderAlreadyExistsException
    extends DuplicateElementException
{
    private final IdProviderKey idProviderKey;

    public IdProviderAlreadyExistsException( final IdProviderKey idProviderKey )
    {
        super(
            MessageFormat.format( "Id Provider [{0}] could not be created. A Id Provider with that name already exists", idProviderKey ) );
        this.idProviderKey = idProviderKey;
    }

    public IdProviderKey getIdProviderKey()
    {
        return idProviderKey;
    }
}
