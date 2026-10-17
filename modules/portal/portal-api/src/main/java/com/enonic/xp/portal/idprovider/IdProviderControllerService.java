package com.enonic.xp.portal.idprovider;

import java.io.IOException;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.security.IdProviderKey;

/**
 * Runs the functions an id provider application exports: {@code handle401}, {@code login},
 * {@code logout}, {@code autoLogin} and the others XP calls on the application's behalf. Which
 * id provider is addressed, and which function, is given per call.
 */
public interface IdProviderControllerService
{
    /**
     * Runs the function the params name on the id provider they name, with the request they
     * carry, and returns what the function responded. When the params also carry a servlet
     * response, the function's response is written to it first. Returns null when the id
     * provider has no application or the application does not export the function.
     *
     * @throws IOException when the response cannot be written to the servlet response
     */
    PortalResponse execute( IdProviderControllerExecutionParams params )
        throws IOException;

    /**
     * Tells whether the id provider's application exports the function, without running it, so
     * a caller can decide what to offer before any request is made.
     */
    boolean hasFunction( IdProviderKey idProviderKey, String functionName );
}
