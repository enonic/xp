package com.enonic.xp.portal.impl.url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Multimap;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.url.AssetUrlParams;

final class AssetUrlBuilder
    extends PortalUrlBuilder<AssetUrlParams>
{

    private ModuleKey getModule()
    {
        return new ModuleResolver().
            context( this.context ).
            module( this.params.getModule() ).
            resolve();
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, this.context.getContentPath().toString() );
        appendPart( url, "_" );
        appendPart( url, "asset" );
        appendPart( url, getModule().toString() );
        appendPart( url, this.params.getPath() );
    }

    @Override
    protected String rewriteUri( final String uri )
    {
        //Example of uri: /portal/draft/context/path/_/asset/mymodule/css/my.css
        //Corresponding result: /portal/draft/_/asset/mymodule/css/my.css

        //Builds the regexp that will catch the content path
        final String regexp = buildContentPathRegExp();

        //Creates a matcher for this regexp and this uri
        final Pattern pattern = Pattern.compile( regexp );
        final Matcher matcher = pattern.matcher( uri );

        //Removes the content path and return the uri
        final String rewrittenUri = matcher.replaceFirst( "/_/asset" );
        return rewrittenUri;
    }

    private String buildContentPathRegExp()
    {
        //Example of uri: /portal/draft/context/path/_/asset/mymodule/css/my.css
        //Corresponding regexp: (?:(?:/context)?/path)?/_/asset

        final StringBuilder regexp = new StringBuilder();

        //For each element of the content path, opens a non recorded group
        final ContentPath contentPath = this.context.getContentPath();
        for ( int i = 0; i < contentPath.elementCount(); i++ )
        {
            regexp.append( "(?:" );
        }

        //For each element of the content path, closes the corresponding optional group and encapsulates the previous groups
        for ( int i = 0; i < contentPath.elementCount(); i++ )
        {
            regexp.append( "/" ).append( contentPath.getElement( i ) ).append( ")?" );
        }

        //The content path is located before "/_/asset" in the case of an asset
        regexp.append( "/_/asset" );

        return regexp.toString();
    }
}
