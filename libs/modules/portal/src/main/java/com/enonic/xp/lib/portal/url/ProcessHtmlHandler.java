package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.ProcessHtmlParams;

public final class ProcessHtmlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ProcessHtmlParams params = new ProcessHtmlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.processHtml( params );
    }
}
