package com.enonic.wem.core.content.relationship;


import org.joda.time.DateTime;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.content.relationship.Relationship.newRelationship;

public class RelationshipFactory
{
    private final DateTime createdTime;

    private final UserKey creator;

    private final ContentId fromContent;

    private final QualifiedRelationshipTypeName type;

    public RelationshipFactory( final Builder builder )
    {
        this.createdTime = builder.createdTime;
        this.creator = builder.creator;
        this.fromContent = builder.fromContent;
        this.type = builder.type;
    }

    public Relationship create( final Data toContent )
    {
        final Relationship.Builder builder = newRelationship();
        builder.creator( creator );
        builder.createdTime( createdTime );
        builder.type( type );
        builder.fromContent( fromContent );
        builder.toContent( ContentId.from( toContent.getString() ) );
        builder.managed( toContent.getPath() );
        return builder.build();
    }

    public static Builder newRelationshipFactory()
    {
        return new Builder();
    }

    public static class Builder
    {
        private DateTime createdTime;

        private UserKey creator;

        private ContentId fromContent;

        private QualifiedRelationshipTypeName type;

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

        public Builder fromContent( final ContentId value )
        {
            this.fromContent = value;
            return this;
        }

        public Builder type( final QualifiedRelationshipTypeName value )
        {
            this.type = value;
            return this;
        }

        public RelationshipFactory build()
        {
            return new RelationshipFactory( this );
        }
    }
}
