package com.enonic.xp.web.impl.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.web.handler.WebHandler;

@Component(immediate = true, service = StatusReporter.class)
public final class WebDispatcherReporter
    implements StatusReporter
{
    private final WebDispatcher webDispatcher;

    @Activate
    public WebDispatcherReporter( @Reference final WebDispatcher webDispatcher )
    {
        this.webDispatcher = webDispatcher;
    }

    @Override
    public MediaType getMediaType()
    {
        return MediaType.JSON_UTF_8;
    }

    @Override
    public void report( final OutputStream outputStream )
        throws IOException
    {
        outputStream.write( getReport().toString().getBytes( StandardCharsets.UTF_8 ) );
    }

    @Override
    public String getName()
    {
        return "http.webHandler";
    }

    private JsonNode getReport()
    {
        final ArrayNode json = JsonNodeFactory.instance.arrayNode();
        for ( final WebHandler handler : this.webDispatcher.list() )
        {
            final ObjectNode node = json.addObject();
            node.put( "order", handler.getOrder() );
            node.put( "class", handler.getClass().getName() );
        }

        return json;
    }
}
