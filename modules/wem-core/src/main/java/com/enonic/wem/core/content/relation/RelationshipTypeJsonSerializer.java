package com.enonic.wem.core.content.relation;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.relation.RelationshipType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.content.AbstractJsonSerializer;
import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;

public class RelationshipTypeJsonSerializer
    extends AbstractJsonSerializer<RelationshipType>
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
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put( "name", relationshipType.getName() );
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

    public RelationshipType toRelationshipType( String json )
        throws JsonParsingException
    {
        return toObject( json );
    }

    protected RelationshipType parse( final JsonNode relationshipTypeNode )
    {
        final RelationshipType.Builder relationshipTypeBuilder = RelationshipType.newRelationType().
            name( JsonParserUtil.getStringValue( "name", relationshipTypeNode ) ).
            module( ModuleName.from( JsonParserUtil.getStringValue( "module", relationshipTypeNode ) ) ).
            fromSemantic( JsonParserUtil.getStringValue( "fromSemantic", relationshipTypeNode ) ).
            toSemantic( JsonParserUtil.getStringValue( "toSemantic", relationshipTypeNode ) );

        final JsonNode allowedFromTypes = relationshipTypeNode.get( "allowedFromTypes" );
        for ( JsonNode allowedFromType : allowedFromTypes )
        {
            relationshipTypeBuilder.addAllowedFromType( new QualifiedContentTypeName( allowedFromType.getValueAsText() ) );
        }

        final JsonNode allowedToTypes = relationshipTypeNode.get( "allowedToTypes" );
        for ( JsonNode allowedToType : allowedToTypes )
        {
            relationshipTypeBuilder.addAllowedToType( new QualifiedContentTypeName( allowedToType.getValueAsText() ) );
        }

        return relationshipTypeBuilder.build();
    }
}
