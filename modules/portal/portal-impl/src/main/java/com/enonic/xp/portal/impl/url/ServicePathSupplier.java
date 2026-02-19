package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;

public class ServicePathSupplier
    implements Supplier<String>
{
    private final String application;

    private final String service;

    public ServicePathSupplier( final String application, final String service )
    {
        this.application = application;
        this.service = service;
    }

    @Override
    public String get()
    {
        final ApplicationKey applicationKey = AppKeyResolver.resolve( application );

        final StringBuilder url = new StringBuilder();
        UrlBuilderHelper.appendSubPath( url, "service" );
        UrlBuilderHelper.appendPart( url, applicationKey.toString() );
        UrlBuilderHelper.appendPart( url, service );

        return url.toString();
    }
}
