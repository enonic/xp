package com.enonic.wem.api.content.relation;


import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.module.ModuleName;

public final class RelationshipType
{
    private final ModuleName module;

    private final String name;

    private final QualifiedRelationshipTypeName qualifiedRelationshipTypeName;

    private final String fromSemantic;

    private final String toSemantic;

    private final QualifiedContentTypeNames allowedFromTypes;

    private final QualifiedContentTypeNames allowedToTypes;

    private RelationshipType( final Builder builder )
    {
        this.module = builder.module;
        this.name = builder.name;
        this.qualifiedRelationshipTypeName = new QualifiedRelationshipTypeName( module, name );
        this.fromSemantic = builder.fromSemantic;
        this.toSemantic = builder.toSemantic;
        this.allowedFromTypes = QualifiedContentTypeNames.from( builder.allowedFromTypes );
        this.allowedToTypes = QualifiedContentTypeNames.from( builder.allowedToTypes );
    }

    public ModuleName getModule()
    {
        return module;
    }

    public String getName()
    {
        return name;
    }

    public QualifiedRelationshipTypeName getQualifiedRelationshipTypeName()
    {
        return qualifiedRelationshipTypeName;
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
        return Objects.equal( this.module, that.module ) &&
            Objects.equal( this.name, that.name ) &&
            Objects.equal( this.qualifiedRelationshipTypeName, that.qualifiedRelationshipTypeName ) &&
            Objects.equal( this.fromSemantic, that.fromSemantic ) &&
            Objects.equal( this.toSemantic, that.toSemantic ) &&
            Objects.equal( this.allowedFromTypes, that.allowedFromTypes ) &&
            Objects.equal( this.allowedToTypes, that.allowedToTypes );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.module, this.name, this.qualifiedRelationshipTypeName, this.fromSemantic, this.toSemantic,
                                 this.allowedFromTypes, this.allowedToTypes );
    }

    public static Builder newRelationType()
    {
        return new Builder();
    }

    public static Builder newRelationType( final RelationshipType relationshipType )
    {
        return new Builder( relationshipType );
    }

    public static class Builder
    {
        private ModuleName module;

        private String name;

        private String fromSemantic;

        private String toSemantic;

        private List<QualifiedContentTypeName> allowedFromTypes = Lists.newArrayList();

        private List<QualifiedContentTypeName> allowedToTypes = Lists.newArrayList();

        private Builder()
        {
            module = null;
            name = null;
            fromSemantic = null;
            toSemantic = null;
            allowedFromTypes = Lists.newArrayList();
            allowedToTypes = Lists.newArrayList();
        }

        private Builder( final RelationshipType relationshipType )
        {
            module = relationshipType.module;
            name = relationshipType.name;
            fromSemantic = relationshipType.fromSemantic;
            toSemantic = relationshipType.toSemantic;
            allowedFromTypes = Lists.newArrayList( allowedFromTypes );
            allowedToTypes = Lists.newArrayList( allowedToTypes );
        }

        public Builder module( ModuleName value )
        {
            this.module = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = value;
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

        public Builder addAllowedFromType( QualifiedContentTypeName value )
        {
            allowedFromTypes.add( value );
            return this;
        }

        public Builder addAllowedToType( QualifiedContentTypeName value )
        {
            allowedToTypes.add( value );
            return this;
        }

        public RelationshipType build()
        {
            return new RelationshipType( this );
        }
    }
}
