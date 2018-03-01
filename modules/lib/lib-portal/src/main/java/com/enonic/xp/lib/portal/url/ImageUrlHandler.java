package com.enonic.xp.lib.portal.url;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.ImageUrlParams;

public final class ImageUrlHandler
    extends AbstractUrlHandler
{
    private final static Set<String> VALID_URL_PROPERTY_KEYS = new HashSet<>(
        Arrays.asList( "id", "path", "scale", "quality", "background", "format", "filter", "contextPath", "type", "params" ) );

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ImageUrlParams params = new ImageUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.imageUrl( params );
    }

    @Override
    protected boolean isValidParam( final String param )
    {
        return VALID_URL_PROPERTY_KEYS.contains( param );
    }
}
