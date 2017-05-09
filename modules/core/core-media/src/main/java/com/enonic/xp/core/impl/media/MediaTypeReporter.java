package com.enonic.xp.core.impl.media;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.MediaType;

import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.util.MediaTypes;

@Component(immediate = true, service = StatusReporter.class)
public final class MediaTypeReporter
    extends JsonStatusReporter
{
    @Override
    public String getName()
    {
        return "mediaTypes";
    }

    @Override
    public JsonNode getReport()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        for ( final Map.Entry<String, MediaType> type : MediaTypes.instance().asMap().entrySet() )
        {
            json.put( type.getKey(), type.getValue().toString() );
        }

        return json;
    }
}
