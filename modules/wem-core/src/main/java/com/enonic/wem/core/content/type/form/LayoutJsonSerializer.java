package com.enonic.wem.core.content.type.form;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.form.FieldSet;
import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItems;
import com.enonic.wem.api.content.type.form.Layout;
import com.enonic.wem.core.content.AbstractJsonSerializer;
import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;

import static com.enonic.wem.api.content.type.form.FieldSet.newFieldSet;

class LayoutJsonSerializer
    extends AbstractJsonSerializer<Layout>
{
    private static final String TYPE = "type";

    private static final String NAME = "name";

    public static final String LABEL = "label";

    public static final String ITEMS = "items";

    private final FormItemsJsonSerializer formItemsJsonSerializer;

    LayoutJsonSerializer( final FormItemsJsonSerializer formItemsJsonSerializer )
    {
        this.formItemsJsonSerializer = formItemsJsonSerializer;
    }

    @Override
    protected JsonNode serialize( final Layout layout, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put( TYPE, FieldSet.class.getSimpleName() );

        if ( layout instanceof FieldSet )
        {
            generateFieldSet( (FieldSet) layout, jsonObject, objectMapper );
        }
        return jsonObject;
    }

    private void generateFieldSet( final FieldSet fieldSet, final ObjectNode jsonObject, final ObjectMapper objectMapper )
    {
        jsonObject.put( LABEL, fieldSet.getLabel() );
        jsonObject.put( NAME, fieldSet.getName() );
        jsonObject.put( ITEMS, formItemsJsonSerializer.serialize( fieldSet.getFormItems(), objectMapper ) );
    }

    public Layout parse( final JsonNode formItemNode )
    {
        final String layoutType = JsonParserUtil.getStringValue( TYPE, formItemNode );
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
        builder.label( JsonParserUtil.getStringValue( LABEL, formItemNode, null ) );
        builder.name( JsonParserUtil.getStringValue( NAME, formItemNode, null ) );

        final FormItems formItems = formItemsJsonSerializer.parse( formItemNode.get( ITEMS ) );
        for ( FormItem formItem : formItems.iterable() )
        {
            builder.add( formItem );
        }

        return builder.build();
    }
}
