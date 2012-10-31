package com.enonic.wem.core.content.type.component;


import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.api.content.type.component.Component;
import com.enonic.wem.api.content.type.component.Components;
import com.enonic.wem.core.content.JsonParsingException;

public class ComponentsSerializerJson
{
    private ComponentSerializerJson componentSerializer = new ComponentSerializerJson( this );

    public void generate( Components components, JsonGenerator g )
        throws IOException
    {
        g.writeArrayFieldStart( "items" );
        for ( Component component : components.iterable() )
        {
            componentSerializer.generate( component, g );
        }
        g.writeEndArray();
    }

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
