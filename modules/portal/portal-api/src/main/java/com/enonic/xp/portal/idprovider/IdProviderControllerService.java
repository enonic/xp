package com.enonic.xp.portal.idprovider;

import java.io.IOException;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.security.IdProviderKey;

public interface IdProviderControllerService
{
    /**
     * Executes the id provider controller function and returns its raw result mapped to a plain Java
     * value - a {@code Map} for a function that returns an object (e.g. the {@code configure} hook), a
     * {@code List} for an array, or a scalar. Returns {@code null} if the function is absent or returns
     * nothing. Use this for hooks that hand data back to core; for hooks that render a response use
     * {@link #executeResponse(IdProviderControllerExecutionParams)}.
     */
    Object executeFunction( IdProviderControllerExecutionParams params )
        throws IOException;

    /**
     * Executes a response-producing controller function (the page hooks, and {@code login} /
     * {@code logout} / {@code autoLogin} / {@code handle401}) and serializes its result to a
     * {@link PortalResponse}. Returns {@code null} if the function is absent or returns nothing.
     */
    PortalResponse executeResponse( IdProviderControllerExecutionParams params )
        throws IOException;

    /**
     * @deprecated renamed to {@link #executeResponse(IdProviderControllerExecutionParams)}. For hooks
     * that return parsed data rather than a response, use
     * {@link #executeFunction(IdProviderControllerExecutionParams)}.
     */
    @Deprecated
    default PortalResponse execute( final IdProviderControllerExecutionParams params )
        throws IOException
    {
        return executeResponse( params );
    }

    /**
     * Whether the id provider's controller implements the given function. Used to decide if a flow
     * is supported by the id provider - e.g. the device/native login page hooks.
     */
    boolean hasFunction( IdProviderKey idProviderKey, String functionName );
}
