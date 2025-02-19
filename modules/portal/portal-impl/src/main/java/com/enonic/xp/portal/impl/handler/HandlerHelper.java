package com.enonic.xp.portal.impl.handler;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;
import com.google.common.primitives.Longs;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

public final class HandlerHelper
{

    private HandlerHelper()
    {
    }

    public static String findPreRestPath( final WebRequest req, final String endpoint )
    {
        final String rawPath = req.getRawPath();
        final int endpointPathIndex = rawPath.indexOf( "/_/" );
        return rawPath.substring( 0, endpointPathIndex + ( "/_/" + endpoint ).length() );
    }

    public static String findRestPath( final WebRequest req, final String endpoint )
    {
        final String endpointPath = req.getEndpointPath();
        final String value = "/_/" + endpoint + "/";
        return endpointPath.length() > value.length() ? endpointPath.substring( value.length() ) : "";
    }

    public static String getParameter( final WebRequest req, final String name )
    {
        final Collection<String> values = req.getParams().get( name );
        return values.isEmpty() ? null : values.iterator().next();
    }

    public static String removeParameter( final PortalRequest req, final String name )
    {
        final Collection<String> values = req.getParams().removeAll( name );
        return values.isEmpty() ? null : values.iterator().next();
    }

    public static PortalResponse handleDefaultOptions( final EnumSet<HttpMethod> methodsAllowed )
    {
        return PortalResponse.create()
            .status( HttpStatus.OK )
            .header( HttpHeaders.ALLOW, methodsAllowed.stream().map( Object::toString ).collect( Collectors.joining( "," ) ) )
            .build();
    }

    public static Long getSize( final WebResponse webResponse )
    {
        final String length = webResponse.getHeaders().get( HttpHeaders.CONTENT_LENGTH );
        if ( length != null )
        {
            return Longs.tryParse( length );
        }
        else
        {
            try
            {
                return getBodyLength( webResponse.getBody() );
            }
            catch ( IOException e )
            {
                return null;
            }
        }
    }

    public static Long getBodyLength( final Object body )
        throws IOException
    {
        if ( body instanceof Resource )
        {
            return ( (Resource) body ).getSize();
        }

        if ( body instanceof ByteSource )
        {
            return ( (ByteSource) body ).size();
        }

        if ( body instanceof Map )
        {
            return null; // TODO
        }

        if ( body instanceof byte[] )
        {
            return (long) ( (byte[]) body ).length;
        }

        if ( body != null )
        {
            return (long) body.toString().length();
        }
        return 0L;
    }

    public static void addTraceInfo( final Trace trace, final WebResponse webResponse )
    {
        if ( trace != null )
        {
            trace.put( "status", webResponse.getStatus().value() );
            trace.put( "type", webResponse.getContentType().toString() );
            trace.put( "size", getSize( webResponse ) );
        }
    }

    public static ProjectName resolveProjectName( final String value )
    {
        try
        {
            return ProjectName.from( value );
        }
        catch ( Exception e )
        {
            throw WebException.notFound( String.format( "Project [%s] not found", value ) );
        }
    }

    public static RepositoryId resolveRepositoryId( final String value )
    {
        try
        {
            return RepositoryId.from( value );
        }
        catch ( Exception e )
        {
            throw WebException.notFound( String.format( "Repository [%s] not found", value ) );
        }
    }

    public static Branch resolveBranch( final String value )
    {
        try
        {
            return Branch.from( value );
        }
        catch ( Exception e )
        {
            throw WebException.notFound( String.format( "Branch [%s] not found", value ) );
        }
    }

    public static ApplicationKey resolveApplicationKey( final String appKey )
    {
        try
        {
            return ApplicationKey.from( appKey );
        }
        catch ( Exception e )
        {
            throw WebException.notFound( String.format( "Application key [%s] not found", appKey ) );
        }
    }

}
