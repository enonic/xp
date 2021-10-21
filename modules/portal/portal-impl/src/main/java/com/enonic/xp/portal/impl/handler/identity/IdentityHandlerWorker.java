package com.enonic.xp.portal.impl.handler.identity;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.WebException;

final class IdentityHandlerWorker
    extends PortalHandlerWorker<PortalRequest>
{
    IdProviderKey idProviderKey;

    IdProviderControllerService idProviderControllerService;

    String idProviderFunction;

    ContentResolver contentResolver;

    IdentityHandlerWorker( final PortalRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {
        final ContentResolverResult resolvedContent = contentResolver.resolve( this.request );

        this.request.setContent( resolvedContent.getContent() );
        this.request.setSite( resolvedContent.getNearestSite() );

        final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create().
            idProviderKey( idProviderKey ).
            functionName( idProviderFunction ).
            portalRequest( this.request ).
            build();

        final PortalResponse portalResponse = idProviderControllerService.execute( executionParams );

        if ( portalResponse == null )
        {
            throw WebException.notFound(
                String.format( "ID Provider function [%s] not found for id provider [%s]", idProviderFunction, idProviderKey ) );
        }
        else
        {
            return portalResponse;
        }
    }
}
