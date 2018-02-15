package com.enonic.xp.web.session.impl.reporter;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.status.StatusReporter;

import static com.enonic.xp.web.session.impl.WebSessionFilter.WEB_SESSION_CACHE;

@SuppressWarnings("unused")
@Component(immediate = true, service = StatusReporter.class)
public class WebSessionReporter
    extends JsonStatusReporter
{
    private Ignite ignite;

    @Override
    public JsonNode getReport()
    {
        final IgniteCache<Object, Object> cache = this.ignite.cache( WEB_SESSION_CACHE );

        if ( cache == null )
        {
            return createEmptyReport();
        }

        return WebSessionReport.create().
            cache( cache ).
            build().
            toJson();
    }

    private ObjectNode createEmptyReport()
    {
        final ObjectNode jsonNodes = JsonNodeFactory.instance.objectNode();
        jsonNodes.put( "status", "no cache found" );
        return jsonNodes;
    }

    @Override
    public String getName()
    {
        return "cache." + WEB_SESSION_CACHE;
    }

    @Reference
    public void setIgnite( final Ignite ignite )
    {
        this.ignite = ignite;
    }
}
