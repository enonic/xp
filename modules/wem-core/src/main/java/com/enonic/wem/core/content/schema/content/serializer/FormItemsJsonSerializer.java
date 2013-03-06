package com.enonic.wem.core.content.schema.content.serializer;


import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import com.enonic.wem.api.content.schema.content.form.FormItem;
import com.enonic.wem.api.content.schema.content.form.FormItems;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonParsingException;

public final class FormItemsJsonSerializer
    extends AbstractJsonSerializer<FormItems>
{
    private final FormItemJsonSerializer formItemSerializer;


    public FormItemsJsonSerializer()
    {
        formItemSerializer = new FormItemJsonSerializer( this, objectMapper() );
    }

    public FormItemsJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
        formItemSerializer = new FormItemJsonSerializer( this, objectMapper );
    }

    public FormItemJsonSerializer getFormItemJsonSerializer()
    {
        return formItemSerializer;
    }

    @Override
    public JsonNode serialize( final FormItems formItems )
    {
        final ArrayNode formItemsArray = objectMapper().createArrayNode();
        for ( FormItem formItem : formItems )
        {
            final JsonNode formItemNode = formItemSerializer.serialize( formItem );
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
