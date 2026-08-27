package com.enonic.xp.portal.idprovider;

import java.io.IOException;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.security.IdProviderKey;

public interface IdProviderControllerService
{
    PortalResponse execute( IdProviderControllerExecutionParams params )
        throws IOException;

    /**
     * Whether the id provider's controller implements the given function.
     */
    boolean hasFunction( IdProviderKey idProviderKey, String functionName );
}
