package com.enonic.wem.core.content.type.formitem;


import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.api.content.type.formitem.FormItem;
import com.enonic.wem.api.content.type.formitem.FormItems;
import com.enonic.wem.core.content.JsonParsingException;

public class FormItemsSerializerJson
{
    private FormItemSerializerJson formItemSerializer = new FormItemSerializerJson( this );

    public void generate( FormItems formItems, JsonGenerator g )
        throws IOException
    {
        g.writeArrayFieldStart( "items" );
        for ( FormItem formItem : formItems.iterable() )
        {
            formItemSerializer.generate( formItem, g );
        }
        g.writeEndArray();
    }

    public FormItems parse( final JsonNode formItemsNode )
    {
        final FormItems formItems = new FormItems();
        final Iterator<JsonNode> formItemIt = formItemsNode.getElements();
        while ( formItemIt.hasNext() )
        {
            try
            {
                final JsonNode formItemNode = formItemIt.next();
                formItems.addFormItem( formItemSerializer.parse( formItemNode ) );
            }
            catch ( Exception e )
            {
                throw new JsonParsingException( "Failed to parse FormItem: " + formItemsNode.toString(), e );
            }
        }

        return formItems;
    }
}
