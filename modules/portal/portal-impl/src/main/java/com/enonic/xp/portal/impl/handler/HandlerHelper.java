package com.enonic.xp.portal.impl.handler;

import java.util.Collection;
import java.util.EnumSet;
import java.util.stream.Collectors;

import com.google.common.net.HttpHeaders;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

public final class HandlerHelper
{

    private HandlerHelper()
    {
    }

    public static String findEndpointPath( final WebRequest req, final String endpoint )
    {
        final String endpointPath = req.getEndpointPath();
        if ( endpointPath == null || !endpointPath.startsWith( "/" + endpoint + "/" ) )
        {
            throw WebException.badRequest( "Unexpected endpoint path: " + endpointPath );
        }
        return endpointPath.substring( endpoint.length() + 2 );
    }

    /**
     * Finds the endpoint name from the request's endpoint path.
     * The endpoint path is expected to start with / followed by the endpoint name and optionally more path segments.
     *
     * @return the api name or null if the request does not have an endpoint path
     */
    public static String findEndpoint( final WebRequest request )
    {
        final String endpointPath = request.getEndpointPath();
        if ( endpointPath == null || endpointPath.charAt( 0 ) != '/' )
        {
            return null;
        }
        final int end = endpointPath.indexOf( '/', 1 );
        return endpointPath.substring( 1, end == -1 ? endpointPath.length() : end );
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

    public static DescriptorKey resolveDescriptorKey( final String descriptorKey )
    {
        try
        {
            final DescriptorKey key = DescriptorKey.from( descriptorKey );
            if ( key.getName().isEmpty() )
            {
                throw WebException.notFound( String.format( "Descriptor key [%s] not found", descriptorKey ) );
            }
            return key;
        }
        catch ( Exception e )
        {
            throw WebException.notFound( String.format( "Descriptor key [%s] not found", descriptorKey ) );
        }
    }
}
