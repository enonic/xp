package com.enonic.xp.schema.relationship;


import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;

@PublicApi
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
        this.allowedFromTypes = ContentTypeNames.from( builder.allowedFromTypes.build() );
        this.allowedToTypes = ContentTypeNames.from( builder.allowedToTypes.build() );
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
        return Objects.equals( this.getName(), that.getName() ) && Objects.equals( this.getDisplayName(), that.getDisplayName() ) &&
            Objects.equals( this.getDescription(), that.getDescription() ) && Objects.equals( this.fromSemantic, that.fromSemantic ) &&
            Objects.equals( this.toSemantic, that.toSemantic ) && Objects.equals( this.allowedFromTypes, that.allowedFromTypes ) &&
            Objects.equals( this.allowedToTypes, that.allowedToTypes );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getName(), getDisplayName(), getDescription(), fromSemantic, toSemantic, allowedFromTypes, allowedToTypes );
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

        private ImmutableList.Builder<ContentTypeName> allowedFromTypes = ImmutableList.builder();

        private ImmutableList.Builder<ContentTypeName> allowedToTypes = ImmutableList.builder();

        private Builder()
        {
            super();
        }

        private Builder( final RelationshipType relationshipType )
        {
            super( relationshipType );
            this.fromSemantic = relationshipType.fromSemantic;
            this.toSemantic = relationshipType.toSemantic;
            this.allowedFromTypes = ImmutableList.<ContentTypeName>builder().addAll( relationshipType.allowedFromTypes );
            this.allowedToTypes = ImmutableList.<ContentTypeName>builder().addAll( relationshipType.allowedToTypes );
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
            allowedFromTypes = new ImmutableList.Builder<ContentTypeName>().addAll( contentTypeNames );
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
            allowedToTypes = new ImmutableList.Builder<ContentTypeName>().addAll( contentTypeNames );
            return this;
        }

        public RelationshipType build()
        {
            return new RelationshipType( this );
        }
    }
}
