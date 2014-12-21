package com.enonic.wem.portal.internal.controller;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.wem.portal.internal.postprocess.PostProcessor;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.script.ScriptValue;

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

        if ( !this.scriptExports.hasMethod( methodName ) )
        {
            populateResponse( context.getResponse(), null );
            return;
        }

        final ScriptValue result = this.scriptExports.executeMethod( methodName, request );
        populateResponse( context.getResponse(), result );
    }

    private void populateResponse( final PortalResponse response, final ScriptValue result )
    {
        response.setStatus( PortalResponse.STATUS_METHOD_NOT_ALLOWED );

        if ( ( result == null ) || !result.isObject() )
        {
            return;
        }

        populateStatus( response, result.getMember( "status" ).getValue( Integer.class ) );
        populateContentType( response, result.getMember( "contentType" ).getValue( String.class ) );
        populateBody( response, result.getMember( "body" ).getValue() );
        populateHeaders( response, result.getMember( "headers" ) );
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

    private void populateHeaders( final PortalResponse response, final ScriptValue value )
    {
        if ( !value.isObject() )
        {
            return;
        }

        for ( final String key : value.getKeys() )
        {
            response.addHeader( key, value.getMember( key ).getValue( String.class ) );
        }
    }
}
