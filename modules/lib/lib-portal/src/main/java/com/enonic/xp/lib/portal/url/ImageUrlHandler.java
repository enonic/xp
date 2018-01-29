package com.enonic.xp.lib.portal.url;

import com.enonic.xp.portal.url.ImageUrlParams;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.List;

public final class ImageUrlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ImageUrlParams params = new ImageUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.imageUrl( params );
    }

    @Override
    protected List<String> getValidUrlPropertyKeys() {
        return Arrays.asList("id", "path", "scale", "quality", "background", "format", "filter",
                "type", "params");
    }
}
