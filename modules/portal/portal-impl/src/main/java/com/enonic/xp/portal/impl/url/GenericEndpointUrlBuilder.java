package com.enonic.xp.portal.impl.url;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.base.Splitter;
import com.google.common.collect.Multimap;
import com.google.common.net.UrlEscapers;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.url.AbstractUrlParams;
import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.web.servlet.UriRewritingResult;

abstract class GenericEndpointUrlBuilder<T extends AbstractUrlParams>
    extends PortalUrlBuilder<T>
{
    private static final String ELEMENT_DIVIDER = "/";

    protected final String endpointType;

    GenericEndpointUrlBuilder( final String endpointType )
    {
        this.endpointType = endpointType;
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        UrlBuilderHelper.appendAndEncodePathParts( url, this.portalRequest.getContentPath().toString() );
        UrlBuilderHelper.appendPart( url, "_" );
        UrlBuilderHelper.appendPart( url, this.endpointType );
    }

    @Override
    protected String postUriRewriting( final UriRewritingResult uriRewritingResult )
    {
        if ( ContextPathType.RELATIVE == this.params.getContextPathType() )
        {
            return uriRewritingResult.getRewrittenUri();
        }

        //Example of URI: /site/repo/draft/context/path/_/asset/myapplication/css/my.css
        //Corresponding result: /site/repo/draft/_/asset/myapplication/css/my.css

        StringBuilder result = new StringBuilder();
        String uriToProcess = uriRewritingResult.getRewrittenUri();

        //If the URI has been rewritten, the rewritten prefix cannot be removed
        if ( uriRewritingResult.getNewUriPrefix() != null )
        {
            result.append( uriRewritingResult.getNewUriPrefix() );
            uriToProcess = uriToProcess.substring( uriRewritingResult.getNewUriPrefix().length() );
        }

        //Gets the part of the URI before the endpoint
        final int indexOfEndpoint = uriToProcess.indexOf( "/_/" + endpointType );
        if ( indexOfEndpoint < 1 )
        {
            return uriRewritingResult.getRewrittenUri();
        }
        final String preEndpointPath = uriToProcess.substring( 0, indexOfEndpoint );

        //Appends the part of the URI before the endpoint minus the matching content path
        result.append( removeContentPath( preEndpointPath ) );

        //Appends the endpoint part
        result.append( result.length() == 1 ? uriToProcess.substring( indexOfEndpoint + 1 ) : uriToProcess.substring( indexOfEndpoint ) );
        return result.toString();
    }

    private String removeContentPath( final String path )
    {
        final String[] splitPreEndpointPath = path.split( "/" );
        int preEndpointPathIndex = splitPreEndpointPath.length - 1;
        final ContentPath normalizedContentPath = ContentPath.from( normalizePath( this.portalRequest.getContentPath().toString() ) );
        int contentPathIndex = normalizedContentPath.elementCount() - 1;
        while ( preEndpointPathIndex >= 0 && contentPathIndex >= 0 &&
            normalizedContentPath.getElement( contentPathIndex ).equals( splitPreEndpointPath[preEndpointPathIndex] ) )
        {
            preEndpointPathIndex--;
            contentPathIndex--;
        }

        final String[] preEndpointPathWithoutContentPath = Arrays.copyOfRange( splitPreEndpointPath, 0, preEndpointPathIndex + 1 );
        return String.join( ELEMENT_DIVIDER, preEndpointPathWithoutContentPath );
    }

    private String normalizePath( final String value )
    {
        return StreamSupport.stream( Splitter.on( '/' ).trimResults().omitEmptyStrings().split( value ).spliterator(), false )
            .map( this::urlEncodePathSegment )
            .collect( Collectors.joining( "/" ) );
    }

    private String urlEncodePathSegment( final String value )
    {
        return UrlEscapers.urlPathSegmentEscaper().escape( value );
    }
}
