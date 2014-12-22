package com.enonic.wem.portal.internal.controller;

import com.enonic.wem.portal.internal.mapper.PortalContextMapper;
import com.enonic.wem.portal.internal.postprocess.PostProcessor;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.script.ScriptValue;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.PortalRequest;
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
        final PortalRequest request = context;
        final String method = context.getRequest().getMethod();
        final String methodName = method.toLowerCase();

        if ( !this.scriptExports.hasMethod( methodName ) )
        {
            populateResponse( context.getResponse(), null );
            return;
        }

        final PortalContextMapper requestMapper = new PortalContextMapper( context );
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
