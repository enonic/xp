package com.enonic.xp.portal.impl.filter;

import java.util.List;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.web.HttpMethod;

public final class PortalRequestSerializer
{
    private final PortalRequest sourceRequest;

    private final ScriptValue value;

    public PortalRequestSerializer( final PortalRequest sourceRequest, final ScriptValue value )
    {
        this.value = value;
        this.sourceRequest = sourceRequest;
    }

    public PortalRequest serialize()
    {
        final PortalRequest req = new PortalRequest( sourceRequest );
        if ( ( value == null ) || !value.isObject() )
        {
            return req;
        }

        req.setBaseUri( sourceRequest.getBaseUri() );
        req.setContentPath( sourceRequest.getContentPath() );
        req.setContent( sourceRequest.getContent() );
        req.setSite( sourceRequest.getSite() );
        req.setComponent( sourceRequest.getComponent() );
        req.setApplicationKey( sourceRequest.getApplicationKey() );
        req.setPageDescriptor( sourceRequest.getPageDescriptor() );
        req.setControllerScript( sourceRequest.getControllerScript() );
        req.setRepositoryId( sourceRequest.getRepositoryId() );

        populateMethod( req, value.getMember( "method" ) );
        populateScheme( req, value.getMember( "scheme" ) );
        populateHost( req, value.getMember( "host" ) );
        populatePort( req, value.getMember( "port" ) );
        populatePath( req, value.getMember( "path" ) );
        populateUrl( req, value.getMember( "url" ) );
        populateRemoteAddress( req, value.getMember( "remoteAddress" ) );
        populateMode( req, value.getMember( "mode" ) );
        populateValidTicket( req, value.getMember( "validTicket" ) );
        populateBranch( req, value.getMember( "branch" ) );
        populateContentType( req, value.getMember( "contentType" ) );

        populateBody( req, value.getMember( "body" ) );

        populateHeaders( req, value.getMember( "headers" ) );
        populateCookies( req, value.getMember( "cookies" ) );
        populateParams( req, value.getMember( "params" ) );

        return req;
    }

    private void populateContentType( final PortalRequest req, final ScriptValue scriptValue )
    {
        final String value = ( scriptValue != null ) ? scriptValue.getValue( String.class ) : null;
        if ( value != null )
        {
            req.setContentType( value );
        }
    }

    private void populateMethod( final PortalRequest req, final ScriptValue value )
    {
        final String method = ( value != null ) ? value.getValue( String.class ) : null;
        if ( method != null )
        {
            req.setMethod( HttpMethod.valueOf( method.toUpperCase() ) );
        }
    }

    private void populateScheme( final PortalRequest req, final ScriptValue value )
    {
        final String scheme = ( value != null ) ? value.getValue( String.class ) : null;
        if ( scheme != null )
        {
            req.setScheme( scheme );
        }
    }

    private void populateHost( final PortalRequest req, final ScriptValue value )
    {
        final String host = ( value != null ) ? value.getValue( String.class ) : null;
        if ( host != null )
        {
            req.setHost( host );
        }
    }

    private void populatePort( final PortalRequest req, final ScriptValue value )
    {
        final Integer port = ( value != null ) ? value.getValue( Integer.class ) : null;
        if ( port != null )
        {
            req.setPort( port );
        }
    }

    private void populatePath( final PortalRequest req, final ScriptValue scriptValue )
    {
        final String value = ( scriptValue != null ) ? scriptValue.getValue( String.class ) : null;
        if ( value != null )
        {
            req.setPath( value );
        }
    }

    private void populateUrl( final PortalRequest req, final ScriptValue scriptValue )
    {
        final String value = ( scriptValue != null ) ? scriptValue.getValue( String.class ) : null;
        if ( value != null )
        {
            req.setUrl( value );
        }
    }

    private void populateRemoteAddress( final PortalRequest req, final ScriptValue scriptValue )
    {
        final String value = ( scriptValue != null ) ? scriptValue.getValue( String.class ) : null;
        if ( value != null )
        {
            req.setRemoteAddress( value );
        }
    }

    private void populateMode( final PortalRequest req, final ScriptValue scriptValue )
    {
        final String mode = ( scriptValue != null ) ? scriptValue.getValue( String.class ) : null;
        if ( mode != null )
        {
            req.setMode( RenderMode.from( mode, req.getMode() ) );
        }
    }

    private void populateValidTicket( final PortalRequest req, final ScriptValue scriptValue )
    {
        final Boolean value = ( scriptValue != null ) ? scriptValue.getValue( Boolean.class ) : null;
        if ( value != null )
        {
            req.setValidTicket( value );
        }
    }

    private void populateBranch( final PortalRequest req, final ScriptValue scriptValue )
    {
        final String value = ( scriptValue != null ) ? scriptValue.getValue( String.class ) : null;
        if ( value != null )
        {
            req.setBranch( Branch.from( value ) );
        }
    }

    private void populateBody( final PortalRequest req, final ScriptValue value )
    {
        if ( ( value == null ) || value.isFunction() )
        {
            return;
        }

        if ( value.isArray() )
        {
            req.setBody( value.getList() );
            return;
        }

        if ( value.isObject() )
        {
            req.setBody( value.getMap() );
            return;
        }

        req.setBody( value.getValue() );
    }

    private void populateHeaders( final PortalRequest req, final ScriptValue value )
    {
        if ( value == null || !value.isObject() )
        {
            return;
        }

        for ( final String key : value.getKeys() )
        {
            req.getHeaders().put( key, value.getMember( key ).getValue( String.class ) );
        }
    }

    private void populateCookies( final PortalRequest req, final ScriptValue value )
    {
        if ( value == null || !value.isObject() )
        {
            return;
        }

        for ( final String key : value.getKeys() )
        {
            final ScriptValue cookieValue = value.getMember( key );
            if ( cookieValue != null && cookieValue.isValue() )
            {
                req.getCookies().put( key, cookieValue.getValue().toString() );
            }
        }
    }

    private void populateParams( final PortalRequest req, final ScriptValue value )
    {
        if ( value == null || !value.isObject() )
        {
            return;
        }

        for ( final String key : value.getKeys() )
        {
            final ScriptValue paramValue = value.getMember( key );
            if ( paramValue == null )
            {
                req.getParams().removeAll( key );
            }
            else if ( paramValue.isArray() )
            {
                req.getParams().replaceValues( key, paramValue.getArray( String.class ) );
            }
            else if ( paramValue.isValue() )
            {
                req.getParams().replaceValues( key, List.of( paramValue.getValue( String.class ) ) );
            }
        }
    }
}
