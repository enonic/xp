package com.enonic.xp.portal.impl.url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Multimap;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.web.servlet.UriRewritingResult;

final class AssetUrlBuilder
    extends PortalUrlBuilder<AssetUrlParams>
{

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, this.portalRequest.getContentPath().toString() );
        appendPart( url, "_" );
        appendPart( url, "asset" );

        Application application = resolveApplication();
        String applicationKey = application.getKey().toString();
        String modifiedTime = Long.toString( application.getModifiedTime().getEpochSecond() );
        appendPart( url, applicationKey + ":" + modifiedTime );

        appendPart( url, this.params.getPath() );
    }

    private Application resolveApplication()
    {
        final ApplicationKey applicationKey = new ApplicationResolver().
            portalRequest( this.portalRequest ).
            application( this.params.getApplication() ).
            resolve();

        final Application application = this.applicationService.getApplication( applicationKey );
        if ( application == null )
        {
            throw new IllegalArgumentException( "Could not find application [" + applicationKey + "]" );
        }
        return application;
    }


    @Override
    protected String postUriRewriting( final UriRewritingResult uriRewritingResult )
    {
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
        result.append( matcher.replaceFirst( "_/asset" ) );
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
            regexp.append( contentPath.getElement( i ) ).append( "/" ).append( ")?" );
        }

        //The content path is located before "/_/asset" in the case of an asset
        regexp.append( "_/asset" );

        return regexp.toString();
    }
}
