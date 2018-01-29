package com.enonic.xp.lib.portal.url;

import com.enonic.xp.portal.url.ServiceUrlParams;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.List;

public final class ServiceUrlHandler
        extends AbstractUrlHandler {

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ServiceUrlParams params = new ServiceUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.serviceUrl( params );
    }

    @Override
    protected List<String> getValidUrlPropertyKeys() {
        return Arrays.asList("application", "service", "type", "params");
    }
}
