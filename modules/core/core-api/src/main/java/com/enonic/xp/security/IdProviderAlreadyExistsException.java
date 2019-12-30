package com.enonic.xp.security;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.BaseException;

@PublicApi
public class IdProviderAlreadyExistsException
    extends BaseException
{
    private final IdProviderKey idProviderKey;

    public IdProviderAlreadyExistsException( final IdProviderKey idProviderKey )
    {
        super( "Id Provider [{0}] could not be created. A Id Provider with that name already exists", idProviderKey );
        this.idProviderKey = idProviderKey;
    }

    public IdProviderKey getIdProviderKey()
    {
        return idProviderKey;
    }
}
