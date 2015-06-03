package com.enonic.xp.portal.impl.controller;

import javax.ws.rs.core.Response;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.mapper.PortalRequestMapper;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.portal.script.ScriptValue;

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
    public void execute( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        PortalRequestAccessor.set( portalRequest );

        try
        {
            doExecute( portalRequest, portalResponse );
            this.postProcessor.processResponse( portalRequest, portalResponse );
        }
        finally
        {
            PortalRequestAccessor.remove();
        }
    }

    private void doExecute( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        final String method = portalRequest.getMethod().toLowerCase();
        final boolean isHead = "head".equals( method );
        final String runMethod = isHead ? "get" : method;

        boolean exists = this.scriptExports.hasMethod( runMethod );
        if ( !exists )
        {
            populateResponse( portalResponse, null );
            return;
        }

        final PortalRequestMapper requestMapper = new PortalRequestMapper( portalRequest );
        final ScriptValue result = this.scriptExports.executeMethod( runMethod, requestMapper );

        populateResponse( portalResponse, result );
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
        populateContributions( response, result.getMember( "pageContributions" ) );
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


    private void populateContributions( final PortalResponse response, final ScriptValue value )
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
            if ( "headBegin".equals( key ) )
            {
                addContribution( response, HtmlTag.HEAD_BEGIN, value.getMember( key ) );
            }
            else if ( "headEnd".equals( key ) )
            {
                addContribution( response, HtmlTag.HEAD_END, value.getMember( key ) );
            }
            else if ( "bodyBegin".equals( key ) )
            {
                addContribution( response, HtmlTag.BODY_BEGIN, value.getMember( key ) );
            }
            else if ( "bodyEnd".equals( key ) )
            {
                addContribution( response, HtmlTag.BODY_END, value.getMember( key ) );
            }
        }
    }

    private void addContribution( final PortalResponse response, final HtmlTag htmlTag, final ScriptValue value )
    {
        if ( value == null )
        {
            return;
        }

        if ( value.isArray() )
        {
            for ( ScriptValue arrayValue : value.getArray() )
            {
                final String strValue = arrayValue.getValue( String.class );
                if ( strValue != null )
                {
                    response.addContribution( htmlTag, strValue );
                }
            }
        }
        else
        {
            final String strValue = value.getValue( String.class );
            if ( strValue != null )
            {
                response.addContribution( htmlTag, strValue );
            }
        }
    }

}
