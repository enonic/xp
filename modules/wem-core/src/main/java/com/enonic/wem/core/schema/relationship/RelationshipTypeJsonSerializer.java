package com.enonic.wem.core.schema.relationship;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;
import com.enonic.wem.core.support.serializer.ParsingException;

public class RelationshipTypeJsonSerializer
    extends AbstractJsonSerializer<RelationshipType>
    implements RelationshipTypeSerializer
{

    public RelationshipTypeJsonSerializer()
    {
    }

    public RelationshipTypeJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
    }

    public JsonNode serialize( final RelationshipType relationshipType )
    {
        final ObjectMapper mapper = objectMapper();
        final ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put( "name", relationshipType.getName() );
        objectNode.put( "displayName", relationshipType.getDisplayName() );
        objectNode.put( "module", relationshipType.getModuleName().toString() );
        objectNode.put( "fromSemantic", relationshipType.getFromSemantic() );
        objectNode.put( "toSemantic", relationshipType.getToSemantic() );
        final ArrayNode allowedFromTypes = objectNode.putArray( "allowedFromTypes" );
        final ArrayNode allowedToTypes = objectNode.putArray( "allowedToTypes" );
        for ( QualifiedContentTypeName allowedFromType : relationshipType.getAllowedFromTypes() )
        {
            allowedFromTypes.add( allowedFromType.toString() );
        }
        for ( QualifiedContentTypeName allowedToType : relationshipType.getAllowedToTypes() )
        {
            allowedToTypes.add( allowedToType.toString() );
        }
        return objectNode;
    }

    @Override
    public RelationshipType toRelationshipType( final String json )
        throws ParsingException
    {
        return toObject( json );
    }

    protected RelationshipType parse( final JsonNode relationshipTypeNode )
    {
        final RelationshipType.Builder relationshipTypeBuilder = RelationshipType.newRelationshipType().
            name( JsonSerializerUtil.getStringValue( "name", relationshipTypeNode ) ).
            displayName( JsonSerializerUtil.getStringValue( "displayName", relationshipTypeNode, null ) ).
            module( ModuleName.from( JsonSerializerUtil.getStringValue( "module", relationshipTypeNode ) ) ).
            fromSemantic( JsonSerializerUtil.getStringValue( "fromSemantic", relationshipTypeNode ) ).
            toSemantic( JsonSerializerUtil.getStringValue( "toSemantic", relationshipTypeNode ) );

        final JsonNode allowedFromTypes = relationshipTypeNode.get( "allowedFromTypes" );
        for ( JsonNode allowedFromType : allowedFromTypes )
        {
            relationshipTypeBuilder.addAllowedFromType( new QualifiedContentTypeName( allowedFromType.textValue() ) );
        }

        final JsonNode allowedToTypes = relationshipTypeNode.get( "allowedToTypes" );
        for ( JsonNode allowedToType : allowedToTypes )
        {
            relationshipTypeBuilder.addAllowedToType( new QualifiedContentTypeName( allowedToType.textValue() ) );
        }

        return relationshipTypeBuilder.build();
    }
}
