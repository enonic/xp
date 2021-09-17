package com.enonic.xp.lib.portal.url;

import java.util.Set;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.ProcessHtmlParams;

public final class ProcessHtmlHandler
    extends AbstractUrlHandler
{
    private static final Set<String> VALID_URL_PROPERTY_KEYS = Set.of( "value", "type", "imageWidths", "imageSizes" );

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ProcessHtmlParams params = new ProcessHtmlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.processHtml( params );
    }

    @Override
    protected boolean isValidParam( final String param )
    {
        return VALID_URL_PROPERTY_KEYS.contains( param );
    }
}
