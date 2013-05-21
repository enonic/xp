package com.enonic.wem.api.content.relationship;


import java.util.Objects;

import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.DataPath;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

/**
 * The natural key of a Relationship.
 */
public final class RelationshipKey
{
    private final QualifiedRelationshipTypeName type;

    private final ContentId fromContent;

    private final ContentId toContent;

    private final DataPath managingData;

    private RelationshipKey( final QualifiedRelationshipTypeName type, final ContentId fromContent, final ContentId toContent,
                             final DataPath managingData )
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

    public DataPath getManagingData()
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

    public static RelationshipKey from( final QualifiedRelationshipTypeName type, final ContentId fromContent, final ContentId toContent )
    {
        return new RelationshipKey( type, fromContent, toContent, null );
    }

    public static RelationshipKey from( final QualifiedRelationshipTypeName type, final ContentId fromContent, final DataPath managingData,
                                        final ContentId toContent )
    {
        return new RelationshipKey( type, fromContent, toContent, managingData );
    }

    public static RelationshipKey from( final ObjectNode objectNode )
    {
        Preconditions.checkArgument( objectNode.has( "type" ), "missing required field: type" );
        final QualifiedRelationshipTypeName type = QualifiedRelationshipTypeName.from( objectNode.get( "type" ).asText() );

        Preconditions.checkArgument( objectNode.has( "fromContent" ), "missing required field: fromContent" );
        final ContentId fromContent = new ContentId( objectNode.get( "fromContent" ).asText() );

        Preconditions.checkArgument( objectNode.has( "toContent" ), "missing required field: toContent" );
        final ContentId toContent = new ContentId( objectNode.get( "toContent" ).asText() );

        return new RelationshipKey( type, fromContent, toContent, null );
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

        private DataPath managingData;

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

        public Builder managingData( DataPath dataPath )
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
