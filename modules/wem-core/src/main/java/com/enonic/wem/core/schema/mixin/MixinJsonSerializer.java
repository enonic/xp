package com.enonic.wem.core.schema.mixin;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.form.FormItemSet;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.schema.content.serializer.FormItemJsonSerializer;
import com.enonic.wem.core.schema.content.serializer.FormItemSetJsonSerializer;
import com.enonic.wem.core.schema.content.serializer.FormItemsJsonSerializer;
import com.enonic.wem.core.schema.content.serializer.InputJsonSerializer;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonParsingException;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

public class MixinJsonSerializer
    extends AbstractJsonSerializer<Mixin>
    implements MixinSerializer
{
    private FormItemsJsonSerializer formItemsSerializer;

    private FormItemSetJsonSerializer formItemSetJsonSerializer;

    private InputJsonSerializer inputSerializer;

    private FormItemJsonSerializer formItemSerializer;


    public MixinJsonSerializer()
    {
        formItemsSerializer = new FormItemsJsonSerializer( objectMapper() );
        inputSerializer = new InputJsonSerializer( objectMapper() );
        formItemSerializer = formItemsSerializer.getFormItemJsonSerializer();
        formItemSetJsonSerializer = new FormItemSetJsonSerializer( formItemsSerializer, objectMapper() );
    }

    @Override
    protected JsonNode serialize( final Mixin mixin )
    {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put( "name", mixin.getName() );
        objectNode.put( "module", mixin.getModuleName().toString() );
        objectNode.put( "displayName", mixin.getDisplayName() );

        objectNode.putAll( (ObjectNode) formItemSerializer.serialize( mixin.getFormItem() ) );

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
        Class type = resolveType( mixinNode );

        if ( type.equals( Input.class ) )
        {
            return parseInputMixin( mixinNode );
        }
        else if ( type.equals( FormItemSet.class ) )
        {
            return parseFormItemSetMixin( mixinNode );
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported type of Mixin: " + type.getSimpleName() );
        }
    }

    private Mixin parseFormItemSetMixin( final JsonNode mixinNode )
    {
        final Mixin.Builder builder = Mixin.newMixin();
        builder.module( ModuleName.from( JsonSerializerUtil.getStringValue( "module", mixinNode ) ) );
        builder.displayName( JsonSerializerUtil.getStringValue( "displayName", mixinNode ) );
        final JsonNode formItemNode = mixinNode.get( FormItemSet.class.getSimpleName() );
        builder.formItem( formItemSetJsonSerializer.parse( formItemNode ) );
        return builder.build();
    }

    private Mixin parseInputMixin( final JsonNode mixinNode )
    {
        final Mixin.Builder builder = Mixin.newMixin();
        builder.module( ModuleName.from( JsonSerializerUtil.getStringValue( "module", mixinNode ) ) );
        builder.displayName( JsonSerializerUtil.getStringValue( "displayName", mixinNode ) );
        final JsonNode inputNode = mixinNode.get( Input.class.getSimpleName() );
        builder.formItem( inputSerializer.parse( inputNode ) );
        return builder.build();
    }

    private Class resolveType( final JsonNode mixinNode )
    {
        JsonNode node = mixinNode.get( FormItemSet.class.getSimpleName() );
        if ( node != null )
        {
            return FormItemSet.class;
        }

        node = mixinNode.get( Input.class.getSimpleName() );
        if ( node != null )
        {
            return Input.class;
        }

        throw new JsonParsingException( "Unrecognised Mixin: " + mixinNode.toString() );
    }
}
