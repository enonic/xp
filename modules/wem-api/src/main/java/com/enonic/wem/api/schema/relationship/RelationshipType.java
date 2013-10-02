package com.enonic.wem.api.schema.relationship;


import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.BaseSchema;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.support.illegaledit.IllegalEdit;
import com.enonic.wem.api.support.illegaledit.IllegalEditAware;

public final class RelationshipType
    extends BaseSchema
    implements Schema, IllegalEditAware<RelationshipType>
{
    private final String fromSemantic;

    private final String toSemantic;

    private final QualifiedContentTypeNames allowedFromTypes;

    private final QualifiedContentTypeNames allowedToTypes;

    private RelationshipType( final Builder builder )
    {
        super( builder );
        this.fromSemantic = builder.fromSemantic;
        this.toSemantic = builder.toSemantic;
        this.allowedFromTypes = QualifiedContentTypeNames.from( builder.allowedFromTypes );
        this.allowedToTypes = QualifiedContentTypeNames.from( builder.allowedToTypes );
    }

    @Override
    public SchemaKey getSchemaKey()
    {
        return SchemaKey.from( getQualifiedName() );
    }

    @Override
    public QualifiedRelationshipTypeName getQualifiedName()
    {
        return new QualifiedRelationshipTypeName( getModuleName(), getName() );
    }

    public String getFromSemantic()
    {
        return fromSemantic;
    }

    public String getToSemantic()
    {
        return toSemantic;
    }

    public QualifiedContentTypeNames getAllowedFromTypes()
    {
        return allowedFromTypes;
    }

    public QualifiedContentTypeNames getAllowedToTypes()
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
        return Objects.equal( this.getModuleName(), that.getModuleName() ) &&
            Objects.equal( this.getName(), that.getName() ) &&
            Objects.equal( this.getDisplayName(), that.getDisplayName() ) &&
            Objects.equal( this.getQualifiedName(), that.getQualifiedName() ) &&
            Objects.equal( this.fromSemantic, that.fromSemantic ) &&
            Objects.equal( this.toSemantic, that.toSemantic ) &&
            Objects.equal( this.allowedFromTypes, that.allowedFromTypes ) &&
            Objects.equal( this.allowedToTypes, that.allowedToTypes );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( getModuleName(), getName(), getDisplayName(), getQualifiedName(), fromSemantic, toSemantic,
                                 allowedFromTypes, allowedToTypes );
    }

    public void checkIllegalEdit( final RelationshipType to )
    {
        Preconditions.checkArgument( this.getCreatedTime().equals( to.getCreatedTime() ) );
        IllegalEdit.check( "createdTime", this.getCreatedTime(), to.getCreatedTime(), RelationshipType.class );

        // Cannot be changed since they are a part of a Relationship's storage path in JCR.
        IllegalEdit.check( "name", this.getName(), to.getName(), RelationshipType.class );
        IllegalEdit.check( "moduleName", this.getModuleName(), to.getModuleName(), RelationshipType.class );
    }

    public static Builder newRelationshipType()
    {
        return new Builder();
    }

    public static Builder newRelationshipType( final RelationshipType relationshipType )
    {
        return new Builder( relationshipType );
    }

    public static class Builder
        extends BaseSchema.Builder<Builder>
    {
        private String fromSemantic;

        private String toSemantic;

        private List<QualifiedContentTypeName> allowedFromTypes = Lists.newArrayList();

        private List<QualifiedContentTypeName> allowedToTypes = Lists.newArrayList();

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

        public Builder addAllowedFromType( QualifiedContentTypeName contentTypeName )
        {
            allowedFromTypes.add( contentTypeName );
            return this;
        }

        public Builder addAllowedFromTypes( Iterable<QualifiedContentTypeName> contentTypeNames )
        {
            for ( QualifiedContentTypeName contentType : contentTypeNames )
            {
                allowedFromTypes.add( contentType );
            }
            return this;
        }

        public Builder setAllowedFromTypes( Iterable<QualifiedContentTypeName> contentTypeNames )
        {
            allowedFromTypes.clear();
            Iterables.addAll( allowedFromTypes, contentTypeNames );
            return this;
        }

        public Builder addAllowedToType( QualifiedContentTypeName contentTypeName )
        {
            allowedToTypes.add( contentTypeName );
            return this;
        }

        public Builder addAllowedToTypes( Iterable<QualifiedContentTypeName> contentTypeNames )
        {
            for ( QualifiedContentTypeName contentType : contentTypeNames )
            {
                allowedToTypes.add( contentType );
            }
            return this;
        }

        public Builder setAllowedToTypes( Iterable<QualifiedContentTypeName> contentTypeNames )
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
