package com.enonic.xp.portal.impl.url;

import com.enonic.xp.portal.url.ApiUrlParams;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendParams;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPathSegments;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendSubPath;

public class SlashApiUrlBuilder
{
    private final ApiUrlParams params;

    public SlashApiUrlBuilder( ApiUrlParams params )
    {
        this.params = params;
    }

    public String build()
    {
        final StringBuilder url = new StringBuilder();
        appendPart( url, "api" );
        appendPart( url, params.getApplication() + ":" + params.getApi() );
        appendSubPath( url, this.params.getPath() );
        appendPathSegments( url, this.params.getPathSegments() );
        appendParams( url, params.getParams().entries() );
        return url.toString();
    }
}
