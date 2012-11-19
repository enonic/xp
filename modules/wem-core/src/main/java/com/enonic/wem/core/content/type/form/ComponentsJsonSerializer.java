package com.enonic.wem.core.content.type.form;


import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItems;
import com.enonic.wem.core.content.AbstractJsonSerializer;
import com.enonic.wem.core.content.JsonParsingException;

public final class ComponentsJsonSerializer
    extends AbstractJsonSerializer<FormItems>
{
    private ComponentJsonSerializer componentSerializer = new ComponentJsonSerializer( this );

    @Override
    public JsonNode serialize( final FormItems formItems, final ObjectMapper objectMapper )
    {
        final ArrayNode jsonComponents = objectMapper.createArrayNode();
        for ( FormItem formItem : formItems )
        {
            final JsonNode jsonComponent = componentSerializer.serialize( formItem, objectMapper );
            jsonComponents.add( jsonComponent );
        }
        return jsonComponents;
    }

    @Override
    public FormItems parse( final JsonNode componentsNode )
    {
        final FormItems formItems = new FormItems();
        final Iterator<JsonNode> componentIt = componentsNode.getElements();
        while ( componentIt.hasNext() )
        {
            try
            {
                final JsonNode componentNode = componentIt.next();
                formItems.add( componentSerializer.parse( componentNode ) );
            }
            catch ( Exception e )
            {
                throw new JsonParsingException( "Failed to parse FormItem: " + componentsNode.toString(), e );
            }
        }

        return formItems;
    }
}
