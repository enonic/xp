package com.enonic.wem.api.content.relationship;


import java.util.Properties;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeName;

public final class Relationship
{
    private RelationshipId id;

    private final DateTime createdTime;

    private final UserKey creator;

    private final QualifiedRelationshipTypeName type;

    private final ContentId fromContent;

    private final ContentId toContent;

    private final Properties properties;

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
        this.properties = builder.properties;
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

    public String getProperty( String name )
    {
        return properties.getProperty( name );
    }

    public boolean isManaged()
    {
        return managed;
    }

    public EntryPath getManagingData()
    {
        return managingData;
    }

    public static Builder newRelationship()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RelationshipId id;

        private UserKey creator;

        private DateTime createdTime;

        private QualifiedRelationshipTypeName type;

        private ContentId fromContent;

        private ContentId toContent;

        private Properties properties = new Properties();

        private boolean managed = false;

        private EntryPath managingData;

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

        public Builder properties( Properties value )
        {
            Preconditions.checkNotNull( value, "properties cannot be null" );
            this.properties = value;
            return this;
        }

        public Builder property( String key, String value )
        {
            this.properties.setProperty( key, value );
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
