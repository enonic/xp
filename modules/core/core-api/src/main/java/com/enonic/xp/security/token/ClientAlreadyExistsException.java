package com.enonic.xp.security.token;

import java.text.MessageFormat;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.exception.DuplicateElementException;
import com.enonic.xp.security.IdProviderKey;

/**
 * The id provider already has a client with that id.
 */
@NullMarked
public class ClientAlreadyExistsException
    extends DuplicateElementException
{
    private final IdProviderKey idProvider;

    private final String id;

    /**
     * Names the id provider and the client id that clashed.
     */
    public ClientAlreadyExistsException( final IdProviderKey idProvider, final String id )
    {
        super( MessageFormat.format( "Client [{0}] already exists in id provider [{1}]", id, idProvider ) );
        this.idProvider = idProvider;
        this.id = id;
    }

    /**
     * The id provider that already has the client.
     */
    public IdProviderKey getIdProvider()
    {
        return idProvider;
    }

    /**
     * The id that already exists.
     */
    public String getId()
    {
        return id;
    }
}
