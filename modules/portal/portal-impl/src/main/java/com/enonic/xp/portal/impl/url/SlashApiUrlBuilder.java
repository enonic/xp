package com.enonic.xp.portal.impl.url;

import java.util.List;

import com.enonic.xp.portal.url.ApiUrlParams;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendParams;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;

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
        appendPart( url, params.getApplication() );
        if ( params.getApi() != null )
        {
            appendPart( url, params.getApi() );
        }
        final List<String> path = this.params.getPath();
        if ( path != null && !path.isEmpty() )
        {
            path.forEach( pathPart -> appendPart( url, pathPart ) );
        }
        appendParams( url, params.getParams().entries() );
        return url.toString();
    }
}
