package com.enonic.wem.core.content.relationship;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.core.content.dao.ContentIdFactory;
import com.enonic.wem.core.content.relationship.dao.RelationshipIdFactory;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonParsingException;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

public class RelationshipJsonSerializer
    extends AbstractJsonSerializer<Relationship>
{
    private boolean includeId;

    private RelationshipId id;

    public RelationshipJsonSerializer()
    {
    }

    public RelationshipJsonSerializer includeId( boolean value )
    {
        this.includeId = value;
        return this;
    }

    public RelationshipJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
    }

    public JsonNode serialize( final Relationship relationship )
    {
        final ObjectNode objectNode = objectMapper().createObjectNode();
        if ( includeId )
        {
            objectNode.put( "id", relationship.getId().toString() );
        }
        objectNode.put( "creator", relationship.getCreator().toString() );
        JsonSerializerUtil.setDateTimeValue( "createdTime", relationship.getCreatedTime(), objectNode );
        objectNode.put( "type", relationship.getType().toString() );
        objectNode.put( "fromContent", relationship.getFromContent().toString() );
        objectNode.put( "toContent", relationship.getToContent().toString() );
        objectNode.put( "managed", relationship.isManaged() );

        if ( relationship.getManagingData() != null )
        {
            objectNode.put( "managingData", relationship.getManagingData().toString() );
        }
        else
        {
            objectNode.putNull( "managingData" );
        }
        serializeProperties( relationship, objectNode );
        return objectNode;
    }

    private void serializeProperties( final Relationship relationship, final ObjectNode objectNode )
    {

    }

    public Relationship toRelationship( final String json, final RelationshipId id )
        throws JsonParsingException
    {
        this.id = id;
        return toObject( json );
    }

    protected Relationship parse( final JsonNode relationshipNode )
    {
        final Relationship.Builder builder = Relationship.newRelationship();

        if ( id != null )
        {
            builder.id( id );
        }
        else if ( includeId )
        {
            builder.id( RelationshipIdFactory.from( JsonSerializerUtil.getStringValue( "id", relationshipNode ) ) );
        }

        builder.creator( JsonSerializerUtil.getUserKeyValue( "creator", relationshipNode ) );
        builder.createdTime( JsonSerializerUtil.getDateTimeValue( "createdTime", relationshipNode ) );
        builder.type( QualifiedRelationshipTypeName.from( JsonSerializerUtil.getStringValue( "type", relationshipNode ) ) );
        builder.fromContent( ContentIdFactory.from( JsonSerializerUtil.getStringValue( "fromContent", relationshipNode ) ) );
        builder.toContent( ContentIdFactory.from( JsonSerializerUtil.getStringValue( "toContent", relationshipNode ) ) );

        return builder.build();
    }
}
