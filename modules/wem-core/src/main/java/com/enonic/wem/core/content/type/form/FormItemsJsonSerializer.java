package com.enonic.wem.core.content.type.form;


import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItems;
import com.enonic.wem.core.content.AbstractJsonSerializer;
import com.enonic.wem.core.content.JsonParsingException;

public final class FormItemsJsonSerializer
    extends AbstractJsonSerializer<FormItems>
{
    private FormItemJsonSerializer formItemSerializer = new FormItemJsonSerializer( this );

    @Override
    public JsonNode serialize( final FormItems formItems, final ObjectMapper objectMapper )
    {
        final ArrayNode formItemsArray = objectMapper.createArrayNode();
        for ( FormItem formItem : formItems )
        {
            final JsonNode formItemNode = formItemSerializer.serialize( formItem, objectMapper );
            formItemsArray.add( formItemNode );
        }
        return formItemsArray;
    }

    @Override
    public FormItems parse( final JsonNode formItemsNode )
    {
        final FormItems formItems = new FormItems();
        final Iterator<JsonNode> formItemIt = formItemsNode.getElements();
        while ( formItemIt.hasNext() )
        {
            try
            {
                final JsonNode formItemNode = formItemIt.next();
                formItems.add( formItemSerializer.parse( formItemNode ) );
            }
            catch ( Exception e )
            {
                throw new JsonParsingException( "Failed to parse FormItem: " + formItemsNode.toString(), e );
            }
        }

        return formItems;
    }
}
