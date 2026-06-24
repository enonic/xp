package com.enonic.xp.portal.idprovider;

import java.io.IOException;

import com.enonic.xp.portal.PortalResponse;

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
}
