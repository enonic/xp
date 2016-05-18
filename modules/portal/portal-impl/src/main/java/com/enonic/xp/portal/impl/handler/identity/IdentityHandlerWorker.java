package com.enonic.xp.portal.impl.handler.identity;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.auth.AuthControllerExecutionParams;
import com.enonic.xp.portal.auth.AuthControllerService;
import com.enonic.xp.portal.handler.ControllerHandlerWorker;
import com.enonic.xp.security.UserStoreKey;

final class IdentityHandlerWorker
    extends ControllerHandlerWorker
{
    protected UserStoreKey userStoreKey;

    protected AuthControllerService authControllerService;

    protected String idProviderFunction;

    @Override
    public void execute()
        throws Exception
    {
        final AuthControllerExecutionParams executionParams = AuthControllerExecutionParams.create().
            userStoreKey( userStoreKey ).
            functionName( idProviderFunction ).
            request( this.request.getRawRequest() ).
            build();
        final PortalResponse portalResponse = authControllerService.execute( executionParams );

        if ( portalResponse == null )
        {
            throw notFound( "ID Provider function [%s] not found for user store [%s]", idProviderFunction, userStoreKey );
        }
        else
        {
            this.response = PortalResponse.create( portalResponse );
        }
    }
}