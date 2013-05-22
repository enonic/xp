package com.enonic.wem.core.relationship;


import org.joda.time.DateTime;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.relationship.Relationship.newRelationship;

class RelationshipFactory
{
    private final DateTime createdTime;

    private final UserKey creator;

    private final DateTime modifiedTime;

    private final UserKey modifier;

    private final ContentId fromContent;

    RelationshipFactory( final Builder builder )
    {
        this.createdTime = builder.createdTime;
        this.creator = builder.creator;
        this.modifiedTime = builder.modifiedTime;
        this.modifier = builder.modifier;
        this.fromContent = builder.fromContent;
    }

    Relationship create( final Property toContent, final QualifiedRelationshipTypeName type )
    {
        final Relationship.Builder builder = newRelationship();
        builder.creator( creator );
        builder.createdTime( createdTime );
        builder.modifier( modifier );
        builder.modifiedTime( modifiedTime );
        builder.type( type );
        builder.fromContent( fromContent );
        builder.toContent( toContent.getContentId() );
        builder.managed( toContent.getPath() );
        return builder.build();
    }

    static Builder newRelationshipFactory()
    {
        return new Builder();
    }

    static class Builder
    {
        private DateTime createdTime;

        private UserKey creator;

        public DateTime modifiedTime;

        public UserKey modifier;

        private ContentId fromContent;

        public Builder createdTime( final DateTime value )
        {
            this.createdTime = value;
            return this;
        }

        public Builder creator( final UserKey value )
        {
            this.creator = value;
            return this;
        }

        public Builder modifiedTime( final DateTime value )
        {
            this.modifiedTime = value;
            return this;
        }

        public Builder modifier( final UserKey value )
        {
            this.modifier = value;
            return this;
        }

        public Builder fromContent( final ContentId value )
        {
            this.fromContent = value;
            return this;
        }

        public RelationshipFactory build()
        {
            return new RelationshipFactory( this );
        }
    }
}
