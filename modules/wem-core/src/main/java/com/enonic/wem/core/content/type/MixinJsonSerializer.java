package com.enonic.wem.core.content.type;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.FormItemSetMixin;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.InputMixin;
import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.content.AbstractJsonSerializer;
import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.form.FormItemJsonSerializer;
import com.enonic.wem.core.content.type.form.FormItemsJsonSerializer;
import com.enonic.wem.core.content.type.form.InputJsonSerializer;

import static com.enonic.wem.api.content.type.form.FormItemSetMixin.newFormItemSetMixin;

public class MixinJsonSerializer
    extends AbstractJsonSerializer<Mixin>
    implements MixinSerializer
{
    private FormItemsJsonSerializer formItemsSerializer;

    private InputJsonSerializer inputSerializer;

    private FormItemJsonSerializer formItemSerializer;


    public MixinJsonSerializer()
    {
        formItemsSerializer = new FormItemsJsonSerializer( objectMapper() );
        inputSerializer = new InputJsonSerializer( objectMapper() );
        formItemSerializer = formItemsSerializer.getFormItemJsonSerializer();
    }

    @Override
    protected JsonNode serialize( final Mixin mixin )
    {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put( "name", mixin.getName() );
        objectNode.put( "module", mixin.getModuleName().toString() );
        objectNode.put( "qualifiedName", mixin.getQualifiedName().toString() );
        objectNode.put( "displayName", mixin.getDisplayName() );

        if ( mixin instanceof InputMixin )
        {
            final InputMixin inputMixin = (InputMixin) mixin;
            objectNode.putAll( (ObjectNode) formItemSerializer.serialize( inputMixin.getInput() ) );
        }
        else
        {
            FormItemSetMixin formItemSetMixin = (FormItemSetMixin) mixin;
            objectNode.putAll( (ObjectNode) formItemSerializer.serialize( formItemSetMixin.getFormItemSet() ) );
        }

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

    private FormItemSetMixin parseFormItemSetMixin( final JsonNode mixinNode )
    {
        final FormItemSetMixin.Builder builder = newFormItemSetMixin();
        builder.module( ModuleName.from( JsonParserUtil.getStringValue( "module", mixinNode ) ) );
        builder.displayName( JsonParserUtil.getStringValue( "displayName", mixinNode ) );
        builder.formItemSet( (FormItemSet) formItemSerializer.parse( mixinNode ) );
        return builder.build();
    }

    private InputMixin parseInputMixin( final JsonNode mixinNode )
    {
        final InputMixin.Builder builder = InputMixin.newInputMixin();
        builder.module( ModuleName.from( JsonParserUtil.getStringValue( "module", mixinNode ) ) );
        builder.displayName( JsonParserUtil.getStringValue( "displayName", mixinNode ) );
        final JsonNode inputNode = mixinNode.get( Input.class.getSimpleName() );
        builder.input( inputSerializer.parse( inputNode ) );
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
