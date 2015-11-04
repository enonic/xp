package com.enonic.xp.portal.impl.controller;

import javax.servlet.http.Cookie;
import javax.ws.rs.core.Response;

import com.google.common.net.MediaType;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.web.HttpStatus;

public final class PortalResponseSerializer
{
    private final ScriptValue value;

    private final HttpStatus defaultStatus;

    private HttpStatus forceStatus;

    private Boolean forcePostProcess;

    private Boolean forceApplyFilters;

    public PortalResponseSerializer( final ScriptValue value )
    {
        this( value, HttpStatus.OK );
    }

    public PortalResponseSerializer( final ScriptValue value, final HttpStatus defaultStatus )
    {
        this.value = value;
        this.defaultStatus = defaultStatus;
    }

    public PortalResponseSerializer postProcess( final boolean value )
    {
        this.forcePostProcess = value;
        return this;
    }

    public PortalResponseSerializer status( final HttpStatus value )
    {
        this.forceStatus = value;
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
        populateContentType( builder, value.getMember( "contentType" ) );
        populateBody( builder, value.getMember( "body" ) );
        populateHeaders( builder, value.getMember( "headers" ) );
        populateContributions( builder, value.getMember( "pageContributions" ) );
        populateCookies( builder, value.getMember( "cookies" ) );
        populateApplyFilters( builder, value.getMember( "applyFilters" ) );
        setRedirect( builder, value.getMember( "redirect" ) );
        populatePostProcess( builder, value.getMember( "postProcess" ) );

        if ( this.forcePostProcess != null )
        {
            builder.postProcess( this.forcePostProcess );
        }
        if ( this.forceStatus != null )
        {
            builder.status( this.forceStatus );
        }
        if ( this.forceApplyFilters != null )
        {
            builder.applyFilters( this.forceApplyFilters );
        }

        return builder.build();
    }

    private void populatePostProcess( final PortalResponse.Builder builder, final ScriptValue value )
    {
        final Boolean postProcess = ( value != null ) ? value.getValue( Boolean.class ) : null;
        builder.postProcess( postProcess != null ? postProcess : true );
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
            builder.body( value.getMap() );
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
}
