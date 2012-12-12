package com.enonic.wem.core.content.type;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.FormItemSetSubType;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.InputSubType;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.content.AbstractJsonSerializer;
import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.form.FormItemJsonSerializer;
import com.enonic.wem.core.content.type.form.FormItemsJsonSerializer;
import com.enonic.wem.core.content.type.form.InputJsonSerializer;

public class SubTypeJsonSerializer
    extends AbstractJsonSerializer<SubType>
    implements SubTypeSerializer
{
    private FormItemsJsonSerializer formItemsSerializer = new FormItemsJsonSerializer();

    private InputJsonSerializer inputSerializer = new InputJsonSerializer();

    private FormItemJsonSerializer formItemSerializer = formItemsSerializer.getFormItemJsonSerializer();

    @Override
    protected JsonNode serialize( final SubType subType, final ObjectMapper objectMapper )
    {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put( "name", subType.getName() );
        objectNode.put( "module", subType.getModuleName().toString() );
        objectNode.put( "qualifiedName", subType.getQualifiedName().toString() );
        objectNode.put( "displayName", subType.getDisplayName() );

        if ( subType instanceof InputSubType )
        {
            final InputSubType inputSubType = (InputSubType) subType;
            objectNode.putAll( (ObjectNode) formItemSerializer.serialize( inputSubType.getInput(), mapper ) );
        }
        else
        {
            FormItemSetSubType formItemSetSubType = (FormItemSetSubType) subType;
            objectNode.putAll( (ObjectNode) formItemSerializer.serialize( formItemSetSubType.getFormItemSet(), mapper ) );
        }

        return objectNode;
    }

    @Override
    public SubType toSubType( String json )
        throws JsonParsingException
    {
        return toObject( json );
    }

    @Override
    protected SubType parse( final JsonNode subTypeNode )
    {
        Class type = resolveType( subTypeNode );

        if ( type.equals( Input.class ) )
        {
            return parseInputSubType( subTypeNode );
        }
        else if ( type.equals( FormItemSet.class ) )
        {
            return parseFormItemSetSubType( subTypeNode );
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported type of SubType: " + type.getSimpleName() );
        }
    }

    private FormItemSetSubType parseFormItemSetSubType( final JsonNode subTypeNode )
    {
        final FormItemSetSubType.Builder builder = FormItemSetSubType.newFormItemSetSubType();
        builder.module( ModuleName.from( JsonParserUtil.getStringValue( "module", subTypeNode ) ) );
        builder.displayName( JsonParserUtil.getStringValue( "displayName", subTypeNode ) );
        builder.formItemSet( (FormItemSet) formItemSerializer.parse( subTypeNode ) );
        return builder.build();
    }

    private InputSubType parseInputSubType( final JsonNode subTypeNode )
    {
        final InputSubType.Builder builder = InputSubType.newInputSubType();
        builder.module( ModuleName.from( JsonParserUtil.getStringValue( "module", subTypeNode ) ) );
        builder.displayName( JsonParserUtil.getStringValue( "displayName", subTypeNode ) );
        final JsonNode inputNode = subTypeNode.get( Input.class.getSimpleName() );
        builder.input( inputSerializer.parse( inputNode ) );
        return builder.build();
    }

    private Class resolveType( final JsonNode subTypeNode )
    {
        JsonNode node = subTypeNode.get( FormItemSet.class.getSimpleName() );
        if ( node != null )
        {
            return FormItemSet.class;
        }

        node = subTypeNode.get( Input.class.getSimpleName() );
        if ( node != null )
        {
            return Input.class;
        }

        throw new JsonParsingException( "Unrecognised SubType: " + subTypeNode.toString() );
    }
}
