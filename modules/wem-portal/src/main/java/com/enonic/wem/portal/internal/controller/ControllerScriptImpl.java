package com.enonic.wem.portal.internal.controller;

import java.util.Map;

import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.PortalContextAccessor;
import com.enonic.wem.portal.PortalRequest;
import com.enonic.wem.portal.PortalResponse;
import com.enonic.wem.portal.internal.postprocess.PostProcessor;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.api.convert.Converters;

final class ControllerScriptImpl
    implements ControllerScript
{
    private final ScriptExports scriptExports;

    private final PostProcessor postProcessor;

    public ControllerScriptImpl( final ScriptExports scriptExports, final PostProcessor postProcessor )
    {
        this.scriptExports = scriptExports;
        this.postProcessor = postProcessor;
    }

    @Override
    public void execute( final PortalContext context )
    {
        PortalContextAccessor.set( context );

        try
        {
            doExecute( context );
            this.postProcessor.processResponse( context );
        }
        finally
        {
            PortalContextAccessor.remove();
        }
    }

    private void doExecute( final PortalContext context )
    {
        final PortalRequest request = context;
        final String method = context.getRequest().getMethod();
        final String methodName = method.toLowerCase();

        if ( !this.scriptExports.hasProperty( methodName ) )
        {
            populateResponse( context.getResponse(), null );
            return;
        }

        final Object result = this.scriptExports.executeMethod( methodName, request );
        populateResponse( context.getResponse(), result );
    }

    private void populateResponse( final PortalResponse response, final Object result )
    {
        response.setStatus( PortalResponse.STATUS_METHOD_NOT_ALLOWED );
        if ( result instanceof Map )
        {
            populateFromMap( response, (Map<?, ?>) result );
        }
    }

    private void populateFromMap( final PortalResponse response, final Map<?, ?> map )
    {
        populateStatus( response, Converters.convert( map.get( "status" ), Integer.class ) );
        populateContentType( response, Converters.convert( map.get( "contentType" ), String.class ) );
        populateBody( response, map.get( "body" ) );
        populateHeaders( response, map.get( "headers" ) );
    }

    private void populateStatus( final PortalResponse response, final Integer value )
    {
        response.setStatus( value != null ? value : PortalResponse.STATUS_OK );
    }

    private void populateContentType( final PortalResponse response, final String value )
    {
        response.setContentType( value != null ? value : "text/html" );
    }

    private void populateBody( final PortalResponse response, final Object value )
    {
        response.setBody( value );
    }

    private void populateHeaders( final PortalResponse response, final Object value )
    {
        if ( !( value instanceof Map ) )
        {
            return;
        }

        for ( final Map.Entry<?, ?> entry : ( (Map<?, ?>) value ).entrySet() )
        {
            response.addHeader( entry.getKey().toString(), entry.getValue().toString() );
        }
    }
}
