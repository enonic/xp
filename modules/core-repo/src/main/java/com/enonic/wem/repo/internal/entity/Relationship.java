package com.enonic.wem.repo.internal.entity;

import java.time.Instant;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.core.data.PropertyPath;
import com.enonic.xp.core.node.NodeId;
import com.enonic.xp.core.security.PrincipalKey;

public final class Relationship
{
    private final RelationshipId id;

    private final Instant createdTime;

    private final PrincipalKey creator;

    private final Instant modifiedTime;

    private final PrincipalKey modifier;

    private final RelationshipKey key;

    private final ImmutableMap<String, String> properties;

    public Relationship( final Builder builder )
    {
        this.id = builder.id;
        this.key = RelationshipKey.from( builder.type, builder.fromItem, builder.managingData, builder.toItem );
        this.createdTime = builder.createdTime;
        this.creator = builder.creator;
        this.modifiedTime = builder.modifiedTime;
        this.modifier = builder.modifier;
        this.properties = ImmutableMap.copyOf( builder.properties );
    }

    public RelationshipId getId()
    {
        return id;
    }

    public RelationshipKey getKey()
    {
        return key;
    }

    public Instant getCreatedTime()
    {
        return createdTime;
    }

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public PrincipalKey getModifier()
    {
        return modifier;
    }

    public RelationshipTypeName getType()
    {
        return key.getType();
    }

    public NodeId getFromItem()
    {
        return key.getFromItem();
    }

    public NodeId getToItem()
    {
        return key.getToItem();
    }

    public ImmutableMap<String, String> getProperties()
    {
        return properties;
    }

    public String getProperty( String name )
    {
        return properties.get( name );
    }

    public boolean isManaged()
    {
        return key.isManaged();
    }

    public PropertyPath getManagingData()
    {
        return key.getManagingData();
    }

    public static Builder newRelationship()
    {
        return new Builder();
    }

    public static Builder newRelationship( final Relationship relationship )
    {
        return new Builder( relationship );
    }

    public static class Builder
    {
        private RelationshipId id;

        private PrincipalKey creator;

        private Instant createdTime;

        private RelationshipTypeName type;

        private NodeId fromItem;

        private NodeId toItem;

        private Map<String, String> properties = Maps.newLinkedHashMap();

        private PropertyPath managingData;

        private PrincipalKey modifier;

        private Instant modifiedTime;

        private Builder( final Relationship relationship )
        {
            id = relationship.id;
            creator = relationship.creator;
            createdTime = relationship.createdTime;
            modifier = relationship.modifier;
            modifiedTime = relationship.modifiedTime;
            type = relationship.key.getType();
            fromItem = relationship.key.getFromItem();
            toItem = relationship.key.getToItem();
            properties.putAll( relationship.getProperties() );
            managingData = relationship.key.getManagingData();
        }

        private Builder()
        {
            // default
        }

        public Builder id( RelationshipId value )
        {
            this.id = value;
            return this;
        }

        public Builder type( RelationshipTypeName value )
        {
            this.type = value;
            return this;
        }

        public Builder fromItem( NodeId value )
        {
            this.fromItem = value;
            return this;
        }

        public Builder toItem( NodeId value )
        {
            this.toItem = value;
            return this;
        }

        public Builder createdTime( Instant value )
        {
            this.createdTime = value;
            return this;
        }

        public Builder creator( PrincipalKey value )
        {
            this.creator = value;
            return this;
        }

        public Builder modifiedTime( Instant value )
        {
            this.modifiedTime = value;
            return this;
        }

        public Builder modifier( PrincipalKey value )
        {
            this.modifier = value;
            return this;
        }

        public Builder properties( Map<String, String> properties )
        {
            Preconditions.checkNotNull( properties, "properties cannot be null" );
            this.properties = properties;
            return this;
        }

        public Builder property( String key, String value )
        {
            this.properties.put( key, value );
            return this;
        }

        public Builder managed( PropertyPath managingData )
        {
            this.managingData = managingData;
            return this;
        }

        public Relationship build()
        {
            return new Relationship( this );
        }

    }
}
