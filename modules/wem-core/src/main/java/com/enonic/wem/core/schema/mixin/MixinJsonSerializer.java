package com.enonic.wem.core.schema.mixin;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.schema.content.serializer.FormItemsJsonSerializer;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonParsingException;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

public class MixinJsonSerializer
    extends AbstractJsonSerializer<Mixin>
    implements MixinSerializer
{
    private FormItemsJsonSerializer formItemsSerializer;


    public MixinJsonSerializer()
    {
        formItemsSerializer = new FormItemsJsonSerializer( objectMapper() );
    }

    @Override
    protected JsonNode serialize( final Mixin mixin )
    {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put( "name", mixin.getName() );
        objectNode.put( "displayName", mixin.getDisplayName() );

        objectNode.putArray( "items" ).addAll( (ArrayNode) formItemsSerializer.serialize( mixin.getFormItems() ) );

        return objectNode;
    }

    @Override
    public Mixin toMixin( String json )
        throws JsonParsingException
    {
        return toObject( json );
    }

    @Override
    protected Mixin parse( final JsonNode mixinNode )
    {
        final Mixin.Builder builder = Mixin.newMixin();
        builder.name( JsonSerializerUtil.getStringValue( "name", mixinNode ) );
        builder.displayName( JsonSerializerUtil.getStringValue( "displayName", mixinNode ) );

        Iterable<FormItem> formItems = formItemsSerializer.parse( mixinNode.get( "items" ) );
        for ( FormItem formItem : formItems )
        {
            builder.addFormItem( formItem );
        }

        return builder.build();
    }
}
