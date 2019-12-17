package com.enonic.xp.schema.relationship;


import java.util.ArrayList;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;

@Beta
public final class RelationshipType
    extends BaseSchema<RelationshipTypeName>
{
    private final String fromSemantic;

    private final String toSemantic;

    private final ContentTypeNames allowedFromTypes;

    private final ContentTypeNames allowedToTypes;

    private RelationshipType( final Builder builder )
    {
        super( builder );
        this.fromSemantic = builder.fromSemantic;
        this.toSemantic = builder.toSemantic;
        this.allowedFromTypes = ContentTypeNames.from( builder.allowedFromTypes );
        this.allowedToTypes = ContentTypeNames.from( builder.allowedToTypes );
    }

    public String getFromSemantic()
    {
        return fromSemantic;
    }

    public String getToSemantic()
    {
        return toSemantic;
    }

    public ContentTypeNames getAllowedFromTypes()
    {
        return allowedFromTypes;
    }

    public ContentTypeNames getAllowedToTypes()
    {
        return allowedToTypes;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof RelationshipType ) )
        {
            return false;
        }
        final RelationshipType that = (RelationshipType) o;
        return Objects.equal( this.getName(), that.getName() ) &&
            Objects.equal( this.getDisplayName(), that.getDisplayName() ) &&
            Objects.equal( this.getDescription(), that.getDescription() ) &&
            Objects.equal( this.fromSemantic, that.fromSemantic ) &&
            Objects.equal( this.toSemantic, that.toSemantic ) &&
            Objects.equal( this.allowedFromTypes, that.allowedFromTypes ) &&
            Objects.equal( this.allowedToTypes, that.allowedToTypes );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( getName(), getDisplayName(), getDescription(), fromSemantic, toSemantic, allowedFromTypes,
                                 allowedToTypes );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final RelationshipType relationshipType )
    {
        return new Builder( relationshipType );
    }

    public static class Builder
        extends BaseSchema.Builder<Builder, RelationshipTypeName>
    {
        private String fromSemantic;

        private String toSemantic;

        private List<ContentTypeName> allowedFromTypes = new ArrayList<>();

        private List<ContentTypeName> allowedToTypes = new ArrayList<>();

        private Builder()
        {
            super();
        }

        private Builder( final RelationshipType relationshipType )
        {
            super( relationshipType );
            this.fromSemantic = relationshipType.fromSemantic;
            this.toSemantic = relationshipType.toSemantic;
            this.allowedFromTypes = Lists.newArrayList( relationshipType.allowedFromTypes );
            this.allowedToTypes = Lists.newArrayList( relationshipType.allowedToTypes );
        }

        public Builder name( final String value )
        {
            super.name( RelationshipTypeName.from( value ) );
            return this;
        }

        public Builder fromSemantic( String value )
        {
            this.fromSemantic = value;
            return this;
        }

        public Builder toSemantic( String value )
        {
            this.toSemantic = value;
            return this;
        }

        public Builder addAllowedFromType( ContentTypeName contentTypeName )
        {
            allowedFromTypes.add( contentTypeName );
            return this;
        }

        public Builder addAllowedFromTypes( Iterable<ContentTypeName> contentTypeNames )
        {
            for ( ContentTypeName contentType : contentTypeNames )
            {
                allowedFromTypes.add( contentType );
            }
            return this;
        }

        public Builder setAllowedFromTypes( Iterable<ContentTypeName> contentTypeNames )
        {
            allowedFromTypes.clear();
            Iterables.addAll( allowedFromTypes, contentTypeNames );
            return this;
        }

        public Builder addAllowedToType( ContentTypeName contentTypeName )
        {
            allowedToTypes.add( contentTypeName );
            return this;
        }

        public Builder addAllowedToTypes( Iterable<ContentTypeName> contentTypeNames )
        {
            for ( ContentTypeName contentType : contentTypeNames )
            {
                allowedToTypes.add( contentType );
            }
            return this;
        }

        public Builder setAllowedToTypes( Iterable<ContentTypeName> contentTypeNames )
        {
            allowedToTypes.clear();
            Iterables.addAll( allowedToTypes, contentTypeNames );
            return this;
        }

        public RelationshipType build()
        {
            return new RelationshipType( this );
        }
    }
}
