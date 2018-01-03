package com.enonic.xp.portal.impl.url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Multimap;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.url.AbstractUrlParams;
import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.web.servlet.UriRewritingResult;

abstract class GenericEndpointUrlBuilder<T extends AbstractUrlParams>
    extends PortalUrlBuilder<T>
{
    protected final String endpointType;

    public GenericEndpointUrlBuilder( final String endpointType )
    {
        this.endpointType = endpointType;
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, this.portalRequest.getContentPath().toString() );
        appendPart( url, "_" );
        appendPart( url, this.endpointType );
    }

    @Override
    protected String postUriRewriting( final UriRewritingResult uriRewritingResult )
    {
        if ( ContextPathType.RELATIVE == this.params.getContextPathType() )
        {
            return uriRewritingResult.getRewrittenUri();
        }

        //Example of URI: /portal/draft/context/path/_/asset/myapplication/css/my.css
        //Corresponding result: /portal/draft/_/asset/myapplication/css/my.css

        StringBuilder result = new StringBuilder();
        String uriToProcess = uriRewritingResult.getRewrittenUri();

        //If the URI has been rewritten, the rewritten prefix cannot be removed
        if ( uriRewritingResult.getNewUriPrefix() != null )
        {
            result.append( uriRewritingResult.getNewUriPrefix() );
            uriToProcess = uriToProcess.substring( uriRewritingResult.getNewUriPrefix().length() );
        }

        //Builds the regexp that will catch the content path
        final String regexp = buildContentPathRegExp();

        //Creates a matcher for this regexp and this uri
        final Pattern pattern = Pattern.compile( regexp );
        final Matcher matcher = pattern.matcher( uriToProcess );

        //Removes the content path and return the uri
        result.append( matcher.replaceFirst( "_/" + endpointType ) );
        return result.toString();
    }

    private String buildContentPathRegExp()
    {
        //Example of uri: /portal/draft/context/path/_/asset/myapplication/css/my.css
        //Corresponding regexp: (?:(?:context/)?path/)?_/asset

        final StringBuilder regexp = new StringBuilder();

        //For each element of the content path, opens a non recorded group
        final ContentPath contentPath = this.portalRequest.getContentPath();
        for ( int i = 0; i < contentPath.elementCount(); i++ )
        {
            regexp.append( "(?:" );
        }

        //For each element of the content path, closes the corresponding optional group and encapsulates the previous groups
        for ( int i = 0; i < contentPath.elementCount(); i++ )
        {
            final String elementPath = Pattern.quote( contentPath.getElement( i ) );
            regexp.append( elementPath ).
                append( "/)?" );
        }

        //The content path is located before "/_/asset" in the case of an asset
        regexp.append( "_/" + endpointType );

        return regexp.toString();
    }
}
