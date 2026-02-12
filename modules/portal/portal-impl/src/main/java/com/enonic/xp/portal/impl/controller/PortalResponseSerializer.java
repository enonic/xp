package com.enonic.xp.portal.impl.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import jakarta.servlet.http.Cookie;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.sse.SseConfig;
import com.enonic.xp.web.websocket.WebSocketConfig;

public final class PortalResponseSerializer
{
    private final ScriptValue value;

    private final HttpStatus defaultStatus;

    private boolean defaultPostProcess;

    private MediaType defaultContentType = MediaType.HTML_UTF_8;

    private Boolean forceApplyFilters;

    public PortalResponseSerializer( final ScriptValue value )
    {
        this( value, HttpStatus.OK );
    }

    public PortalResponseSerializer( final ScriptValue value, final HttpStatus defaultStatus )
    {
        this.value = value;
        this.defaultStatus = Objects.requireNonNull( defaultStatus );
        this.defaultPostProcess = true;
    }

    public PortalResponseSerializer defaultPostProcess( final boolean defaultValue )
    {
        this.defaultPostProcess = defaultValue;
        return this;
    }

    public PortalResponseSerializer applyFilters( final boolean value )
    {
        this.forceApplyFilters = value;
        return this;
    }

    public PortalResponse serialize()
    {
        PortalResponse.Builder builder = PortalResponse.create();
        builder.status( this.defaultStatus );

        if ( ( value == null ) || !value.isObject() )
        {
            return builder.build();
        }

        populateStatus( builder, value.getMember( "status" ) );
        populateBody( builder, value.getMember( "body" ) );
        populateContentType( builder, value.getMember( "contentType" ) );
        populateHeaders( builder, value.getMember( "headers" ) );
        populateContributions( builder, value.getMember( "pageContributions" ) );
        populateCookies( builder, value.getMember( "cookies" ) );
        populateApplyFilters( builder, value.getMember( "applyFilters" ) );
        setRedirect( builder, value.getMember( "redirect" ) );
        populatePostProcess( builder, value.getMember( "postProcess" ) );
        populateWebSocket( builder, value.getMember( "webSocket" ) );
        populateSse( builder, value.getMember( "sse" ) );

        if ( this.forceApplyFilters != null )
        {
            builder.applyFilters( this.forceApplyFilters );
        }

        return builder.build();
    }

    private void populatePostProcess( final PortalResponse.Builder builder, final ScriptValue value )
    {
        final Boolean postProcess = value != null ? value.getValue( Boolean.class ) : null;
        builder.postProcess( postProcess != null ? postProcess : defaultPostProcess );
    }

    private void populateStatus( final PortalResponse.Builder builder, final ScriptValue value )
    {
        final Integer status = value != null ? value.getValue( Integer.class ) : null;
        builder.status( status != null ? HttpStatus.from( status ) : defaultStatus );
    }

    private void populateContentType( final PortalResponse.Builder builder, final ScriptValue value )
    {
        final String type = value != null ? value.getValue( String.class ) : null;
        builder.contentType( type != null ? MediaType.parse( type ) : defaultContentType );
    }

    private void setRedirect( final PortalResponse.Builder builder, final ScriptValue value )
    {
        final String redirect = value != null ? value.getValue( String.class ) : null;
        if ( redirect == null )
        {
            return;
        }

        builder.status( HttpStatus.SEE_OTHER );
        builder.header( HttpHeaders.LOCATION, redirect );
    }

    private void populateBody( final PortalResponse.Builder builder, final ScriptValue value )
    {
        if ( value == null || value.isFunction() )
        {
            return;
        }

        if ( value.isArray() )
        {
            builder.body( value.getList() );
            defaultContentType = MediaType.JSON_UTF_8;
            return;
        }

        if ( value.isObject() )
        {
            builder.body( value.getMap() );
            defaultContentType = MediaType.JSON_UTF_8;
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
            final ScriptValue headerValue = value.getMember( key );
            if ( headerValue == null )
            {
                builder.removeHeader( key );
            }
            else
            {
                builder.header( key, headerValue.getValue( String.class ) );
            }
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
            final Cookie cookie = new Cookie( key, "" );
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
                else if ( "sameSite".equals( subKey ) )
                {
                    cookie.setAttribute( "SameSite", value.getMember( subKey ).getValue( String.class ) );
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
            final HtmlTag htmlTag = HtmlTag.from( key );
            if ( htmlTag != null )
            {
                addContribution( builder, htmlTag, value.getMember( key ) );
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

    private void populateApplyFilters( final PortalResponse.Builder builder, final ScriptValue value )
    {
        final Boolean applyFilters = value != null ? value.getValue( Boolean.class ) : null;
        builder.applyFilters( applyFilters != null ? applyFilters : true );
    }

    private void populateWebSocket( final PortalResponse.Builder builder, final ScriptValue value )
    {
        if ( value == null )
        {
            return;
        }

        final WebSocketConfig config = new WebSocketConfig();
        populateWebSocketData( config, value.getMember( "data" ) );
        populateWebSocketSubProtocols( config, value.getMember( "subProtocols" ) );

        builder.webSocket( config );
    }

    private void populateWebSocketData( final WebSocketConfig config, final ScriptValue value )
    {
        if ( value == null )
        {
            return;
        }

        final Map<String, Object> map = value.getMap();
        final Map<String, String> result = new HashMap<>();

        for ( final Map.Entry<String, Object> entry : map.entrySet() )
        {
            result.put( entry.getKey(), entry.getValue().toString() );
        }

        config.setData( result );
    }

    private void populateWebSocketSubProtocols( final WebSocketConfig config, final ScriptValue value )
    {
        if ( value == null )
        {
            return;
        }

        if ( value.isArray() )
        {
            config.setSubProtocols( value.getArray( String.class ) );
        }
        else
        {
            config.setSubProtocols( Collections.singletonList( value.getValue( String.class ) ) );
        }
    }

    private void populateSse( final PortalResponse.Builder builder, final ScriptValue value )
    {
        if ( value == null )
        {
            return;
        }

        final SseConfig config = new SseConfig();
        populateSseData( config, value.getMember( "data" ) );

        builder.sse( config );
    }

    private void populateSseData( final SseConfig config, final ScriptValue value )
    {
        if ( value == null )
        {
            return;
        }

        final Map<String, Object> map = value.getMap();
        final Map<String, String> result = new HashMap<>();

        for ( final Map.Entry<String, Object> entry : map.entrySet() )
        {
            result.put( entry.getKey(), entry.getValue().toString() );
        }

        config.setData( result );
    }
}
