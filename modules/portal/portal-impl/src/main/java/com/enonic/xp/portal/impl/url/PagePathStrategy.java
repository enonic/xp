package com.enonic.xp.portal.impl.url;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.PageUrlGeneratorParams;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendParams;

final class PagePathStrategy
    implements PathStrategy
{
    private final PageUrlGeneratorParams params;

    PagePathStrategy( final PageUrlGeneratorParams params )
    {
        this.params = params;
    }

    @Override
    public String generatePath()
    {
        final Multimap<String, String> queryParams = LinkedListMultimap.create();
        params.getQueryParams().forEach( queryParams::putAll );

        final StringBuilder path = new StringBuilder();
        appendParams( path, queryParams.entries() );
        return path.toString();
    }
}
