package com.enonic.xp.web.impl.handler;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.web.handler.WebHandler;

@Component(immediate = true, service = StatusReporter.class)
public final class WebDispatcherReporter
    extends JsonStatusReporter
{
    private WebDispatcher webDispatcher;

    @Override
    public String getName()
    {
        return "http.webHandler";
    }

    @Override
    public JsonNode getReport()
    {
        final ArrayNode json = JsonNodeFactory.instance.arrayNode();
        for ( final WebHandler handler : this.webDispatcher )
        {
            final ObjectNode node = json.addObject();
            node.put( "order", handler.getOrder() );
            node.put( "class", handler.getClass().getName() );
        }

        return json;
    }

    @Reference
    public void setWebDispatcher( final WebDispatcher webDispatcher )
    {
        this.webDispatcher = webDispatcher;
    }
}
