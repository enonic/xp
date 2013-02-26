package com.enonic.wem.api.content.relationship;


import java.util.Objects;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

/**
 * The natural key of a Relationship.
 */
public final class RelationshipKey
{
    private final QualifiedRelationshipTypeName type;

    private final ContentId fromContent;

    private final ContentId toContent;

    private final EntryPath managingData;

    private RelationshipKey( final QualifiedRelationshipTypeName type, final ContentId fromContent, final ContentId toContent,
                             final EntryPath managingData )
    {
        this.type = type;
        this.fromContent = fromContent;
        this.toContent = toContent;
        this.managingData = managingData;
    }

    private RelationshipKey( final Builder builder )
    {
        this.type = builder.type;
        this.fromContent = builder.fromContent;
        this.toContent = builder.toContent;
        this.managingData = builder.managingData;
    }

    public QualifiedRelationshipTypeName getType()
    {
        return type;
    }

    public ContentId getFromContent()
    {
        return fromContent;
    }

    public EntryPath getManagingData()
    {
        return managingData;
    }

    public ContentId getToContent()
    {
        return toContent;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final RelationshipKey that = (RelationshipKey) o;

        return Objects.equals( type, that.type ) &&
            Objects.equals( fromContent, that.fromContent ) &&
            Objects.equals( toContent, that.toContent ) &&
            Objects.equals( managingData, that.managingData );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( type, fromContent, toContent, managingData );
    }

    public static RelationshipKey from( final QualifiedRelationshipTypeName type, final ContentId fromContent, final ContentId toContent )
    {
        return new RelationshipKey( type, fromContent, toContent, null );
    }

    public static RelationshipKey from( final QualifiedRelationshipTypeName type, final ContentId fromContent, final EntryPath managingData,
                                        final ContentId toContent )
    {
        return new RelationshipKey( type, fromContent, toContent, managingData );
    }

    public static Builder newRelationshipKey()
    {
        return new Builder();
    }

    public static class Builder
    {
        private QualifiedRelationshipTypeName type;

        private ContentId fromContent;

        private ContentId toContent;

        private EntryPath managingData;

        public Builder type( QualifiedRelationshipTypeName relationshipType )
        {
            this.type = relationshipType;
            return this;
        }

        public Builder fromContent( ContentId contentId )
        {
            this.fromContent = contentId;
            return this;
        }

        public Builder toContent( ContentId contentId )
        {
            this.toContent = contentId;
            return this;
        }

        public Builder managingData( EntryPath entryPath )
        {
            this.managingData = entryPath;
            return this;
        }

        public RelationshipKey build()
        {
            return new RelationshipKey( this );
        }
    }
}
