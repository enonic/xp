package com.enonic.wem.core.schema.content.serializer;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import com.enonic.wem.api.schema.content.form.FormItem;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonParsingException;

public final class FormItemsJsonSerializer
    extends AbstractJsonSerializer<Iterable<FormItem>>
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
    public JsonNode serialize( final Iterable<FormItem> formItems )
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
    public Iterable<FormItem> parse( final JsonNode formItemsNode )
    {
        final List<FormItem> formItems = new ArrayList<>();
        final Iterator<JsonNode> formItemIt = formItemsNode.elements();
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
