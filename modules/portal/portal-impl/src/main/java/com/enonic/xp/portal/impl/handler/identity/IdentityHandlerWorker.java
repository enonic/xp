package com.enonic.xp.portal.impl.handler.identity;

import com.enonic.xp.content.Content;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.ControllerHandlerWorker;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.site.Site;

final class IdentityHandlerWorker
    extends ControllerHandlerWorker
{
    protected IdProviderKey idProviderKey;

    protected IdProviderControllerService idProviderControllerService;

    protected String idProviderFunction;

    public IdentityHandlerWorker( final PortalRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {
        //Prepares the request
        Content content = getContentOrNull( getContentSelector() );
        this.request.setContent( content );
        final Site site = getSiteOrNull( content );
        this.request.setSite( site );

        final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create().
            idProviderKey( idProviderKey ).
            functionName( idProviderFunction ).
            portalRequest( this.request ).
            build();

        final PortalResponse portalResponse = idProviderControllerService.execute( executionParams );

        if ( portalResponse == null )
        {
            throw notFound( "ID Provider function [%s] not found for user store [%s]", idProviderFunction, idProviderKey );
        }
        else
        {
            return portalResponse;
        }
    }
}