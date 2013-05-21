package com.enonic.wem.core.schema.content.serializer;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.schema.content.form.FieldSet;
import com.enonic.wem.api.schema.content.form.FormItem;
import com.enonic.wem.api.schema.content.form.Layout;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonParsingException;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

import static com.enonic.wem.api.schema.content.form.FieldSet.newFieldSet;

class LayoutJsonSerializer
    extends AbstractJsonSerializer<Layout>
{
    private static final String TYPE = "type";

    private static final String NAME = "name";

    public static final String LABEL = "label";

    public static final String ITEMS = "items";

    private final FormItemsJsonSerializer formItemsJsonSerializer;

    LayoutJsonSerializer( final FormItemsJsonSerializer formItemsJsonSerializer, final ObjectMapper objectMapper )
    {
        super( objectMapper );
        this.formItemsJsonSerializer = formItemsJsonSerializer;
    }

    @Override
    protected JsonNode serialize( final Layout layout )
    {
        final ObjectNode jsonObject = objectMapper().createObjectNode();
        jsonObject.put( TYPE, FieldSet.class.getSimpleName() );

        if ( layout instanceof FieldSet )
        {
            generateFieldSet( (FieldSet) layout, jsonObject );
        }
        return jsonObject;
    }

    private void generateFieldSet( final FieldSet fieldSet, final ObjectNode jsonObject )
    {
        jsonObject.put( LABEL, fieldSet.getLabel() );
        jsonObject.put( NAME, fieldSet.getName() );
        jsonObject.put( ITEMS, formItemsJsonSerializer.serialize( fieldSet.getFormItems() ) );
    }

    public Layout parse( final JsonNode formItemNode )
    {
        final String layoutType = JsonSerializerUtil.getStringValue( TYPE, formItemNode );
        if ( layoutType.equals( FieldSet.class.getSimpleName() ) )
        {
            return parseFieldSet( formItemNode );
        }
        else
        {
            throw new JsonParsingException( "Unknown layout type: " + layoutType );
        }
    }

    private FieldSet parseFieldSet( final JsonNode formItemNode )
    {
        final FieldSet.Builder builder = newFieldSet();
        builder.label( JsonSerializerUtil.getStringValue( LABEL, formItemNode, null ) );
        builder.name( JsonSerializerUtil.getStringValue( NAME, formItemNode, null ) );

        for ( FormItem formItem : formItemsJsonSerializer.parse( formItemNode.get( ITEMS ) ) )
        {
            builder.add( formItem );
        }

        return builder.build();
    }
}
