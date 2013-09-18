package com.enonic.wem.core.schema.content.serializer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.schema.content.form.FormItem;
import com.enonic.wem.api.schema.content.form.FormItemSet;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

import static com.enonic.wem.api.schema.content.form.FormItemSet.newFormItemSet;

public class FormItemSetJsonSerializer
    extends AbstractJsonSerializer<FormItemSet>
{
    private static final String NAME = "name";

    public static final String LABEL = "label";

    public static final String IMMUTABLE = "immutable";

    public static final String CUSTOM_TEXT = "customText";

    public static final String HELP_TEXT = "helpText";

    public static final String OCCURRENCES = "occurrences";

    public static final String ITEMS = "items";

    private final OccurrencesJsonSerializer occurrencesJsonSerializer;

    private final FormItemsJsonSerializer formItemsJsonSerializer;

    public FormItemSetJsonSerializer( final FormItemsJsonSerializer formItemsJsonSerializer, final ObjectMapper objectMapper )
    {
        super( objectMapper );
        this.formItemsJsonSerializer = formItemsJsonSerializer;
        occurrencesJsonSerializer = new OccurrencesJsonSerializer( objectMapper );
    }

    @Override
    protected JsonNode serialize( final FormItemSet set )
    {
        final ObjectNode jsonObject = objectMapper().createObjectNode();
        jsonObject.put( NAME, set.getName() );
        jsonObject.put( LABEL, set.getLabel() );
        jsonObject.put( IMMUTABLE, set.isImmutable() );
        jsonObject.put( OCCURRENCES, occurrencesJsonSerializer.serialize( set.getOccurrences() ) );
        jsonObject.put( CUSTOM_TEXT, set.getCustomText() );
        jsonObject.put( HELP_TEXT, set.getHelpText() );
        jsonObject.put( ITEMS, formItemsJsonSerializer.serialize( set.getFormItems() ) );
        return jsonObject;
    }


    public FormItemSet parse( final JsonNode formItemSetObj )
    {
        final FormItemSet.Builder builder = newFormItemSet();
        builder.name( JsonSerializerUtil.getStringValue( NAME, formItemSetObj ) );
        builder.label( JsonSerializerUtil.getStringValue( LABEL, formItemSetObj, null ) );
        builder.immutable( JsonSerializerUtil.getBooleanValue( IMMUTABLE, formItemSetObj ) );
        builder.helpText( JsonSerializerUtil.getStringValue( HELP_TEXT, formItemSetObj ) );
        builder.customText( JsonSerializerUtil.getStringValue( CUSTOM_TEXT, formItemSetObj ) );

        parseOccurrences( builder, formItemSetObj.get( OCCURRENCES ) );

        final JsonNode formItemsNode = formItemSetObj.get( ITEMS );
        for ( final FormItem formItem : formItemsJsonSerializer.parse( formItemsNode ) )
        {
            builder.addFormItem( formItem );
        }

        return builder.build();
    }

    private void parseOccurrences( final FormItemSet.Builder builder, final JsonNode occurrencesNode )
    {
        if ( occurrencesNode != null )
        {
            builder.occurrences( occurrencesJsonSerializer.parse( occurrencesNode ) );
        }
        else
        {
            builder.multiple( false );
        }
    }
}
