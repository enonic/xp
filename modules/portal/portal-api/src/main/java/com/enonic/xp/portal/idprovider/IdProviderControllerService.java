package com.enonic.xp.portal.idprovider;

import java.io.IOException;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.security.IdProviderKey;

/**
 * Runs the functions an id provider application exports: {@code handle401}, {@code login},
 * {@code logout}, {@code autoLogin} and the others XP calls.
 */
public interface IdProviderControllerService
{
    /**
     * Runs the named function on the named id provider and returns its response. If the params carry
     * a servlet response, the function's response is written to it first. Returns null if the id
     * provider has no application or the application does not export the function.
     *
     * @throws IOException if the response cannot be written
     */
    PortalResponse execute( IdProviderControllerExecutionParams params )
        throws IOException;

    /**
     * Whether the id provider's application exports the function, without running it.
     */
    boolean hasFunction( IdProviderKey idProviderKey, String functionName );
}
