package com.enonic.xp.portal.impl.idprovider;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

public interface IdProviderControllerScript
{
    boolean hasMethod( String functionName );

    PortalResponse execute( String functionName, PortalRequest portalRequest );

    /**
     * Executes the function passing {@code context} as its second argument (after the request).
     * Used for predefined hooks that need a function-specific context, e.g. the device/native
     * {@code approval} hook.
     */
    PortalResponse execute( String functionName, PortalRequest portalRequest, Object context );
}
