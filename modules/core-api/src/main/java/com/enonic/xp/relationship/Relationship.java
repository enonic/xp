package com.enonic.xp.relationship;


import java.time.Instant;
import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.security.PrincipalKey;

@Beta
public final class Relationship
{
    private final RelationshipId id;

    private final Instant createdTime;

    private final PrincipalKey creator;

    private final Instant modifiedTime;

    private final PrincipalKey modifier;

    private final RelationshipTypeName type;

    private final ContentId fromContent;

    private final ContentId toContent;

    private final ImmutableMap<String, String> properties;

    /**
     * Path to the Data in the fromContent that is managing this Relationship.
     */
    private final PropertyPath managingData;

    public Relationship( final Builder builder )
    {
        this.id = builder.id;
        this.type = builder.type;
        this.fromContent = builder.fromContent;
        this.toContent = builder.toContent;
        this.createdTime = builder.createdTime;
        this.creator = builder.creator;
        this.modifiedTime = builder.modifiedTime;
        this.modifier = builder.modifier;
        this.properties = ImmutableMap.copyOf( builder.properties );
        this.managingData = builder.managingData;
    }

    public RelationshipId getId()
    {
        return id;
    }

    public RelationshipKey getKey()
    {
        return RelationshipKey.from( type, fromContent, managingData, toContent );
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
        return type;
    }

    public ContentId getFromContent()
    {
        return fromContent;
    }

    public ContentId getToContent()
    {
        return toContent;
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
        return managingData != null;
    }

    public PropertyPath getManagingData()
    {
        return managingData;
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

        private ContentId fromContent;

        private ContentId toContent;

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
            type = relationship.type;
            fromContent = relationship.fromContent;
            toContent = relationship.toContent;
            properties.putAll( relationship.getProperties() );
            managingData = relationship.managingData;
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

        public Builder fromContent( ContentId value )
        {
            this.fromContent = value;
            return this;
        }

        public Builder toContent( ContentId value )
        {
            this.toContent = value;
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
