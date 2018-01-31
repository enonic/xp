package com.enonic.xp.lib.portal.url;

import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.List;

public final class ProcessHtmlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ProcessHtmlParams params = new ProcessHtmlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.processHtml( params );
    }

    @Override
    protected List<String> getValidUrlPropertyKeys() {
        return Arrays.asList("value", "type");
    }
}
