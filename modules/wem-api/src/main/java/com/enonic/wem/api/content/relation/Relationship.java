package com.enonic.wem.api.content.relation;


import java.util.Properties;

import org.joda.time.DateTime;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.EntryPath;

public final class Relationship
{
    /**
     * If true, managed by fromContent.
     * It can only be modified through fromContent.
     */
    private boolean managed;

    /**
     * Path to the data in the fromContent that is managing this relationship.
     */
    private EntryPath managingData;

    private final RelationshipType type;

    private final ContentPath fromContent;

    private final ContentPath toContent;

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

    public ContentPath getFromContent()
    {
        return fromContent;
    }

    public ContentPath getToContent()
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

        private ContentPath fromContent;

        private ContentPath toContent;

        private DateTime createdTime;

        private UserKey creator;

        public Builder type( RelationshipType value )
        {
            this.type = value;
            return this;
        }

        public Builder from( ContentPath value )
        {
            this.fromContent = value;
            return this;
        }

        public Builder to( ContentPath value )
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
