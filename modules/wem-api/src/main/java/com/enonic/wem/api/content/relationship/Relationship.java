package com.enonic.wem.api.content.relationship;


import java.util.Properties;

import org.joda.time.DateTime;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.EntryPath;

public final class Relationship
{
    /**
     * If true, managed by fromContent.
     * It can only be modified through fromContent.
     */
    private boolean managed;

    /**
     * Path to the Entry in the fromContent that is managing this relationship.
     */
    private EntryPath managingData;

    private final RelationshipType type;

    private final ContentId fromContent;

    private final ContentId toContent;

    private final DateTime createdTime;

    private final UserKey creator;

    private Properties properties;


    public Relationship( final Builder builder )
    {
        this.type = builder.type;
        this.fromContent = builder.fromContent;
        this.toContent = builder.toContent;
        this.createdTime = builder.createdTime;
        this.creator = builder.creator;
    }

    public RelationshipType getType()
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

    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    public UserKey getCreator()
    {
        return creator;
    }

    public static Builder newRelation()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RelationshipType type;

        private ContentId fromContent;

        private ContentId toContent;

        private DateTime createdTime;

        private UserKey creator;

        public Builder type( RelationshipType value )
        {
            this.type = value;
            return this;
        }

        public Builder from( ContentId value )
        {
            this.fromContent = value;
            return this;
        }

        public Builder to( ContentId value )
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

        public Relationship build()
        {
            return new Relationship( this );
        }
    }
}
