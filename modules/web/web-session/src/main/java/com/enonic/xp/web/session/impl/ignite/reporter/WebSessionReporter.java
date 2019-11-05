package com.enonic.xp.web.session.impl.ignite.reporter;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.web.session.impl.AbstractSessionDataStoreFactoryActivator;

@SuppressWarnings("unused")
@Component(immediate = true, service = StatusReporter.class)
public class WebSessionReporter
    extends JsonStatusReporter
{
    private final Ignite ignite;

    @Activate
    public WebSessionReporter( @Reference Ignite ignite )
    {
        this.ignite = ignite;
    }

    @Override
    public JsonNode getReport()
    {
        final IgniteCache<Object, Object> cache = this.ignite.cache( AbstractSessionDataStoreFactoryActivator.WEB_SESSION_CACHE );

        return WebSessionReport.create().
            cache( cache ).
            build().
            toJson();
    }

    @Override
    public String getName()
    {
        return "cache." + AbstractSessionDataStoreFactoryActivator.WEB_SESSION_CACHE;
    }
}
