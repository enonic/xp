package com.enonic.xp.portal.idprovider;

import java.io.IOException;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.security.IdProviderKey;

public interface IdProviderControllerService
{
    PortalResponse execute( IdProviderControllerExecutionParams params )
        throws IOException;

    /**
     * Executes a predefined controller function that returns a boolean (e.g. the
     * {@code allowRedirectUri} policy hook). Returns {@code false} if the id provider does not
     * implement the function or it does not return a boolean.
     */
    boolean executeBoolean( IdProviderControllerExecutionParams params )
        throws IOException;

    /**
     * Whether the id provider's controller implements the given function. Used to decide if a flow
     * is supported by the id provider - e.g. the device/native login page hooks.
     */
    boolean hasFunction( IdProviderKey idProviderKey, String functionName );
}
