package com.enonic.xp.portal.impl.idprovider;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

public interface IdProviderControllerScript
{
    boolean hasMethod( String functionName );

    PortalResponse execute( String functionName, PortalRequest portalRequest );

    /**
     * Executes the function passing {@code context} as its second argument (after the request).
     * Used for predefined hooks that need a function-specific context, e.g. the device verification
     * and native consent hooks.
     */
    PortalResponse execute( String functionName, PortalRequest portalRequest, Object context );

    /**
     * Executes the function (with {@code context} as its second argument) and returns its boolean
     * result, or {@code false} if it is missing or does not return a boolean.
     */
    boolean executeBoolean( String functionName, PortalRequest portalRequest, Object context );
}
