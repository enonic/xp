package com.enonic.wem.core.content.type.formitem;


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

    public Components parse( final JsonNode formItemsNode )
    {
        final Components components = new Components();
        final Iterator<JsonNode> formItemIt = formItemsNode.getElements();
        while ( formItemIt.hasNext() )
        {
            try
            {
                final JsonNode formItemNode = formItemIt.next();
                components.add( componentSerializer.parse( formItemNode ) );
            }
            catch ( Exception e )
            {
                throw new JsonParsingException( "Failed to parse Component: " + formItemsNode.toString(), e );
            }
        }

        return components;
    }
}
