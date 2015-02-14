package com.enonic.xp.portal.impl.controller;

import javax.ws.rs.core.Response;

import com.enonic.xp.portal.impl.mapper.PortalRequestMapper;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.PortalResponse;

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
        final String method = context.getMethod();
        final String methodName = method.toLowerCase();

        if ( !this.scriptExports.hasMethod( methodName ) )
        {
            populateResponse( context.getResponse(), null );
            return;
        }

        final PortalRequestMapper requestMapper = new PortalRequestMapper( context );
        final ScriptValue result = this.scriptExports.executeMethod( methodName, requestMapper );

        populateResponse( context.getResponse(), result );
    }

    private void populateResponse( final PortalResponse response, final ScriptValue result )
    {
        response.setStatus( PortalResponse.STATUS_METHOD_NOT_ALLOWED );

        if ( ( result == null ) || !result.isObject() )
        {
            return;
        }

        populateStatus( response, result.getMember( "status" ) );
        populateContentType( response, result.getMember( "contentType" ) );
        populateBody( response, result.getMember( "body" ) );
        populateHeaders( response, result.getMember( "headers" ) );
        setRedirect( response, result.getMember( "redirect" ) );
    }

    private void populateStatus( final PortalResponse response, final ScriptValue value )
    {
        final Integer status = ( value != null ) ? value.getValue( Integer.class ) : null;
        response.setStatus( status != null ? status : PortalResponse.STATUS_OK );
    }

    private void populateContentType( final PortalResponse response, final ScriptValue value )
    {
        final String type = ( value != null ) ? value.getValue( String.class ) : null;
        response.setContentType( type != null ? type : "text/html" );
    }

    private void setRedirect( final PortalResponse response, final ScriptValue value )
    {
        final String redirect = ( value != null ) ? value.getValue( String.class ) : null;
        if ( redirect == null )
        {
            return;
        }

        response.setStatus( Response.Status.SEE_OTHER.getStatusCode() );
        response.addHeader( "Location", redirect );
    }

    private void populateBody( final PortalResponse response, final ScriptValue value )
    {
        if ( ( value == null ) || value.isFunction() )
        {
            return;
        }

        if ( value.isArray() )
        {
            response.setBody( value.getValue( String.class ) );
            return;
        }

        if ( value.isObject() )
        {
            response.setBody( value.getMap() );
            return;
        }

        response.setBody( value.getValue() );
    }

    private void populateHeaders( final PortalResponse response, final ScriptValue value )
    {
        if ( value == null )
        {
            return;
        }

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
