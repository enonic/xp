package com.enonic.wem.api.relationship;


import java.util.Objects;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.PropertyPath;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

/**
 * The natural key of a Relationship.
 */
public final class RelationshipKey
{
    private final RelationshipTypeName type;

    private final ContentId fromContent;

    private final ContentId toContent;

    private final PropertyPath managingData;

    private RelationshipKey( final RelationshipTypeName type, final ContentId fromContent, final ContentId toContent,
                             final PropertyPath managingData )
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

    public RelationshipTypeName getType()
    {
        return type;
    }

    public ContentId getFromContent()
    {
        return fromContent;
    }

    public PropertyPath getManagingData()
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
    public String toString()
    {
        return com.google.common.base.Objects.toStringHelper( this ).
            add( "fromContent", fromContent ).
            add( "toContent", toContent ).
            add( "type", type ).
            add( "managingData", managingData ).
            toString();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( type, fromContent, toContent, managingData );
    }

    public static RelationshipKey from( final RelationshipTypeName type, final ContentId fromContent, final ContentId toContent )
    {
        return new RelationshipKey( type, fromContent, toContent, null );
    }

    public static RelationshipKey from( final RelationshipTypeName type, final ContentId fromContent, final PropertyPath managingData,
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
        private RelationshipTypeName type;

        private ContentId fromContent;

        private ContentId toContent;

        private PropertyPath managingData;

        public Builder type( RelationshipTypeName relationshipType )
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

        public Builder managingData( PropertyPath dataPath )
        {
            this.managingData = dataPath;
            return this;
        }

        public RelationshipKey build()
        {
            return new RelationshipKey( this );
        }
    }
}
