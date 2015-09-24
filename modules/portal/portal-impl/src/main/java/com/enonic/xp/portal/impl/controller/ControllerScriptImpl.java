package com.enonic.xp.portal.impl.controller;

import javax.servlet.http.Cookie;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.mapper.PortalRequestMapper;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

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
    public PortalResponse execute( final PortalRequest portalRequest )
    {
        PortalRequestAccessor.set( portalRequest );

        try
        {
            return this.postProcessor.
                processResponse( portalRequest, doExecute( portalRequest ) );
        }
        finally
        {
            PortalRequestAccessor.remove();
        }
    }

    private PortalResponse doExecute( final PortalRequest portalRequest )
    {
        final HttpMethod method = portalRequest.getMethod();
        final boolean isHead = method.equals( HttpMethod.HEAD );
        final String runMethod = isHead ? "get" : method.toString().toLowerCase();

        boolean exists = this.scriptExports.hasMethod( runMethod );
        if ( !exists )
        {
            return createResponse( null );
        }

        final PortalRequestMapper requestMapper = new PortalRequestMapper( portalRequest );
        final ScriptValue result = this.scriptExports.executeMethod( runMethod, requestMapper );

        return createResponse( result );
    }

    private PortalResponse createResponse( final ScriptValue result )
    {
        PortalResponse.Builder builder = PortalResponse.create();
        builder.status( HttpStatus.METHOD_NOT_ALLOWED );

        if ( ( result == null ) || !result.isObject() )
        {
            return builder.build();
        }

        populateStatus( builder, result.getMember( "status" ) );
        populateContentType( builder, result.getMember( "contentType" ) );
        populateBody( builder, result.getMember( "body" ) );
        populateHeaders( builder, result.getMember( "headers" ) );
        populateContributions( builder, result.getMember( "pageContributions" ) );
        populateCookies( builder, result.getMember( "cookies" ) );
        setRedirect( builder, result.getMember( "redirect" ) );

        return builder.build();
    }

    private void populateStatus( final PortalResponse.Builder builder, final ScriptValue value )
    {
        final Integer status = ( value != null ) ? value.getValue( Integer.class ) : null;
        builder.status( status != null ? HttpStatus.from( status ) : HttpStatus.OK );
    }

    private void populateContentType( final PortalResponse.Builder builder, final ScriptValue value )
    {
        final String type = ( value != null ) ? value.getValue( String.class ) : null;
        builder.contentType( type != null ? MediaType.parse( type ) : MediaType.create( "text", "html" ) );
    }

    private void setRedirect( final PortalResponse.Builder builder, final ScriptValue value )
    {
        final String redirect = ( value != null ) ? value.getValue( String.class ) : null;
        if ( redirect == null )
        {
            return;
        }

        builder.status( HttpStatus.from( Response.Status.SEE_OTHER.getStatusCode() ) );
        builder.header( "Location", redirect );
    }

    private void populateBody( final PortalResponse.Builder builder, final ScriptValue value )
    {
        if ( ( value == null ) || value.isFunction() )
        {
            return;
        }

        if ( value.isArray() )
        {
            builder.body( value.getValue( String.class ) );
            return;
        }

        if ( value.isObject() )
        {
            builder.body( convertToJson( value.getMap() ) );
            return;
        }

        builder.body( value.getValue() );
    }

    private void populateHeaders( final PortalResponse.Builder builder, final ScriptValue value )
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
            builder.header( key, value.getMember( key ).getValue( String.class ) );
        }
    }

    private void populateCookies( final PortalResponse.Builder builder, final ScriptValue value )
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
            addCookie( builder, value.getMember( key ), key );
        }
    }

    private void addCookie( final PortalResponse.Builder builder, final ScriptValue value, final String key )
    {
        if ( value == null )
        {
            return;
        }

        if ( value.isObject() )
        {
            Cookie cookie = new Cookie( key, "" );

            for ( final String subKey : value.getKeys() )
            {
                if ( "value".equals( subKey ) )
                {
                    cookie.setValue( value.getMember( subKey ).getValue( String.class ) );
                }
                else if ( "path".equals( subKey ) )
                {
                    cookie.setPath( value.getMember( subKey ).getValue( String.class ) );
                }
                else if ( "domain".equals( subKey ) )
                {
                    cookie.setDomain( value.getMember( subKey ).getValue( String.class ) );
                }
                else if ( "comment".equals( subKey ) )
                {
                    cookie.setComment( value.getMember( subKey ).getValue( String.class ) );
                }
                else if ( "maxAge".equals( subKey ) )
                {
                    cookie.setMaxAge( value.getMember( subKey ).getValue( Integer.class ) );
                }
                else if ( "secure".equals( subKey ) )
                {
                    cookie.setSecure( value.getMember( subKey ).getValue( Boolean.class ) );
                }
                else if ( "httpOnly".equals( subKey ) )
                {
                    cookie.setHttpOnly( value.getMember( subKey ).getValue( Boolean.class ) );
                }
            }
            builder.cookie( cookie );
        }
        else
        {
            final String strValue = value.getValue( String.class );
            if ( strValue != null )
            {
                builder.cookie( new Cookie( key, strValue ) );
            }
            else
            {
                builder.cookie( new Cookie( key, "" ) );
            }
        }
    }

    private void populateContributions( final PortalResponse.Builder builder, final ScriptValue value )
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
                addContribution( builder, HtmlTag.HEAD_BEGIN, value.getMember( key ) );
            }
            else if ( "headEnd".equals( key ) )
            {
                addContribution( builder, HtmlTag.HEAD_END, value.getMember( key ) );
            }
            else if ( "bodyBegin".equals( key ) )
            {
                addContribution( builder, HtmlTag.BODY_BEGIN, value.getMember( key ) );
            }
            else if ( "bodyEnd".equals( key ) )
            {
                addContribution( builder, HtmlTag.BODY_END, value.getMember( key ) );
            }
        }
    }

    private void addContribution( final PortalResponse.Builder builder, final HtmlTag htmlTag, final ScriptValue value )
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
                    builder.contribution( htmlTag, strValue );
                }
            }
        }
        else
        {
            final String strValue = value.getValue( String.class );
            if ( strValue != null )
            {
                builder.contribution( htmlTag, strValue );
            }
        }
    }

    private String convertToJson( final Object value )
    {
        try
        {
            return new ObjectMapper().writeValueAsString( value );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}
