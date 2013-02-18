package com.enonic.wem.api.content.relationship;


import java.util.Map;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.support.illegalchange.IllegalChange;
import com.enonic.wem.api.support.illegalchange.IllegalChangeAware;
import com.enonic.wem.api.support.illegalchange.IllegalChangeException;

public final class Relationship
    implements IllegalChangeAware<Relationship>
{
    private final RelationshipId id;

    private final DateTime createdTime;

    private final UserKey creator;

    private final QualifiedRelationshipTypeName type;

    private final ContentId fromContent;

    private final ContentId toContent;

    private final ImmutableMap<String, String> properties;

    /**
     * If true, managed by fromContent.
     * It can only be modified through fromContent.
     */
    private boolean managed;

    /**
     * Path to the Entry in the fromContent that is managing this relationship.
     */
    private EntryPath managingData;

    public Relationship( final Builder builder )
    {
        this.id = builder.id;
        this.type = builder.type;
        this.fromContent = builder.fromContent;
        this.toContent = builder.toContent;
        this.createdTime = builder.createdTime;
        this.creator = builder.creator;
        this.properties = ImmutableMap.copyOf( builder.properties );
        this.managed = builder.managed;
        if ( this.managed )
        {
            this.managingData = builder.managingData;
        }
    }

    public RelationshipId getId()
    {
        return id;
    }

    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    public UserKey getCreator()
    {
        return creator;
    }

    public QualifiedRelationshipTypeName getType()
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
        return managed;
    }

    public EntryPath getManagingData()
    {
        return managingData;
    }

    @Override
    public void checkIllegalChange( final Relationship to )
        throws IllegalChangeException
    {
        IllegalChange.check( "createdTime", this.getCreatedTime(), to.getCreatedTime(), Relationship.class );
        IllegalChange.check( "creator", this.getCreator(), to.getCreator(), Relationship.class );
        IllegalChange.check( "fromContent", this.getFromContent(), to.getFromContent(), Relationship.class );
        IllegalChange.check( "toContent", this.getToContent(), to.getToContent(), Relationship.class );
        IllegalChange.check( "type", this.getType(), to.getType(), Relationship.class );
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

        private UserKey creator;

        private DateTime createdTime;

        private QualifiedRelationshipTypeName type;

        private ContentId fromContent;

        private ContentId toContent;

        private Map<String, String> properties = Maps.newLinkedHashMap();

        private boolean managed = false;

        private EntryPath managingData;

        private Builder( final Relationship relationship )
        {
            id = relationship.id;
            creator = relationship.creator;
            createdTime = relationship.createdTime;
            type = relationship.type;
            fromContent = relationship.fromContent;
            toContent = relationship.toContent;
            properties.putAll( relationship.getProperties() );
            managed = relationship.managed;
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

        public Builder type( QualifiedRelationshipTypeName value )
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

        public Builder createdTime( DateTime value )
        {
            this.createdTime = value;
            return this;
        }

        public Builder creator( UserKey value )
        {
            this.creator = value;
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

        public Builder managed( EntryPath managingData )
        {
            this.managed = true;
            this.managingData = managingData;
            return this;
        }

        public Relationship build()
        {
            return new Relationship( this );
        }

    }
}
