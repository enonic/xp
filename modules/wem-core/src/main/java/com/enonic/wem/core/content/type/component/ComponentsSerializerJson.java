package com.enonic.wem.core.content.type.component;


import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import com.enonic.wem.api.content.type.component.Component;
import com.enonic.wem.api.content.type.component.Components;
import com.enonic.wem.core.content.AbstractSerializerJson;
import com.enonic.wem.core.content.JsonParsingException;

public final class ComponentsSerializerJson
    extends AbstractSerializerJson<Components>
{
    private ComponentSerializerJson componentSerializer = new ComponentSerializerJson( this );

    @Override
    public JsonNode serialize( final Components components, final ObjectMapper objectMapper )
    {
        final ArrayNode jsonComponents = objectMapper.createArrayNode();
        for ( Component component : components )
        {
            final JsonNode jsonComponent = componentSerializer.serialize( component, objectMapper );
            jsonComponents.add( jsonComponent );
        }
        return jsonComponents;
    }

    @Override
    public Components parse( final JsonNode componentsNode )
    {
        final Components components = new Components();
        final Iterator<JsonNode> componentIt = componentsNode.getElements();
        while ( componentIt.hasNext() )
        {
            try
            {
                final JsonNode componentNode = componentIt.next();
                components.add( componentSerializer.parse( componentNode ) );
            }
            catch ( Exception e )
            {
                throw new JsonParsingException( "Failed to parse Component: " + componentsNode.toString(), e );
            }
        }

        return components;
    }
}
