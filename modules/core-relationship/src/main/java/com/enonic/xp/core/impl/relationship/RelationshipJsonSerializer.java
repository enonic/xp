package com.enonic.xp.core.impl.relationship;


import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.relationship.Relationship;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.support.serializer.AbstractJsonSerializer;
import com.enonic.xp.support.serializer.JsonParsingException;
import com.enonic.xp.support.serializer.JsonSerializerUtil;

public final class RelationshipJsonSerializer
    extends AbstractJsonSerializer<Relationship>
{
    private boolean includeCreator;

    private boolean includeCreatedTime;

    private boolean includeModifier;

    private boolean includeModifiedTime;

    public RelationshipJsonSerializer()
    {
    }

    public RelationshipJsonSerializer includeCreator( boolean value )
    {
        this.includeCreator = value;
        return this;
    }

    public RelationshipJsonSerializer includeCreatedTime( boolean value )
    {
        this.includeCreatedTime = value;
        return this;
    }

    public RelationshipJsonSerializer includeModifier( boolean value )
    {
        this.includeModifier = value;
        return this;
    }

    public RelationshipJsonSerializer includeModifiedTime( boolean value )
    {
        this.includeModifiedTime = value;
        return this;
    }

    @Override
    public JsonNode serialize( final Relationship relationship )
    {
        final ObjectNode objectNode = objectMapper().createObjectNode();
        if ( includeCreator )
        {
            objectNode.put( "creator", relationship.getCreator().toString() );
        }
        if ( includeCreatedTime )
        {
            JsonSerializerUtil.setInstantValue( "createdTime", relationship.getCreatedTime(), objectNode );
        }
        if ( includeModifier )
        {
            objectNode.put( "modifier", relationship.getModifier().toString() );
        }
        if ( includeModifiedTime )
        {
            JsonSerializerUtil.setInstantValue( "modifiedTime", relationship.getModifiedTime(), objectNode );
        }
        objectNode.put( "type", relationship.getType().toString() );
        objectNode.put( "fromContent", relationship.getFromContent().toString() );
        objectNode.put( "toContent", relationship.getToContent().toString() );

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
        final Map<String, String> properties = relationship.getProperties();
        if ( properties.size() == 0 )
        {
            objectNode.putNull( "properties" );
        }
        else
        {
            final ObjectNode propertiesNode = objectNode.putObject( "properties" );
            for ( Map.Entry<String, String> entry : properties.entrySet() )
            {
                propertiesNode.put( entry.getKey(), entry.getValue() );
            }
        }
    }

    public Relationship toRelationship( final String json )
        throws JsonParsingException
    {
        return toObject( json );
    }

    @Override
    protected Relationship parse( final JsonNode relationshipNode )
    {
        final Relationship.Builder builder = Relationship.create();

        if ( includeCreator )
        {
            builder.creator( JsonSerializerUtil.getPrincipalKeyValue( "creator", relationshipNode ) );
        }
        if ( includeCreatedTime )
        {
            builder.createdTime( JsonSerializerUtil.getInstantValue( "createdTime", relationshipNode ) );
        }
        if ( includeModifier )
        {
            builder.modifier( JsonSerializerUtil.getPrincipalKeyValue( "modifier", relationshipNode ) );
        }
        if ( includeModifiedTime )
        {
            builder.modifiedTime( JsonSerializerUtil.getInstantValue( "modifiedTime", relationshipNode ) );
        }
        builder.type( RelationshipTypeName.from( JsonSerializerUtil.getStringValue( "type", relationshipNode ) ) );
        builder.fromContent( ContentId.from( JsonSerializerUtil.getStringValue( "fromContent", relationshipNode ) ) );
        builder.toContent( ContentId.from( JsonSerializerUtil.getStringValue( "toContent", relationshipNode ) ) );
        if ( !relationshipNode.get( "managingData" ).isNull() )
        {
            builder.managed( PropertyPath.from( JsonSerializerUtil.getStringValue( "managingData", relationshipNode ) ) );
        }
        if ( !relationshipNode.get( "properties" ).isNull() )
        {
            final ObjectNode propertiesNode = (ObjectNode) relationshipNode.get( "properties" );
            final Iterator<String> it = propertiesNode.fieldNames();
            while ( it.hasNext() )
            {
                final String fieldName = it.next();
                builder.property( fieldName, propertiesNode.get( fieldName ).textValue() );
            }
        }
        return builder.build();
    }
}
