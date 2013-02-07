package com.enonic.wem.api.content.relationshiptype;


import java.util.List;

import org.joda.time.DateTime;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.AbstractBaseType;
import com.enonic.wem.api.content.BaseType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.module.ModuleName;

public final class RelationshipType
    extends AbstractBaseType
    implements BaseType
{
    private final ModuleName module;

    private final String name;

    private final String displayName;

    private final DateTime createdTime;

    private final DateTime modifiedTime;

    private final QualifiedRelationshipTypeName qualifiedName;

    private final String fromSemantic;

    private final String toSemantic;

    private final QualifiedContentTypeNames allowedFromTypes;

    private final QualifiedContentTypeNames allowedToTypes;

    private final Icon icon;

    private RelationshipType( final Builder builder )
    {
        this.module = builder.module;
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.qualifiedName = new QualifiedRelationshipTypeName( module, name );
        this.fromSemantic = builder.fromSemantic;
        this.toSemantic = builder.toSemantic;
        this.allowedFromTypes = QualifiedContentTypeNames.from( builder.allowedFromTypes );
        this.allowedToTypes = QualifiedContentTypeNames.from( builder.allowedToTypes );
        this.icon = builder.icon;
    }

    public String getName()
    {
        return name;
    }

    public QualifiedRelationshipTypeName getQualifiedName()
    {
        return qualifiedName;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public ModuleName getModuleName()
    {
        return module;
    }

    @Override
    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    @Override
    public DateTime getModifiedTime()
    {
        return modifiedTime;
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

    public Icon getIcon()
    {
        return icon;
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
            Objects.equal( this.displayName, that.displayName ) &&
            Objects.equal( this.qualifiedName, that.qualifiedName ) &&
            Objects.equal( this.fromSemantic, that.fromSemantic ) &&
            Objects.equal( this.toSemantic, that.toSemantic ) &&
            Objects.equal( this.allowedFromTypes, that.allowedFromTypes ) &&
            Objects.equal( this.allowedToTypes, that.allowedToTypes );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( module, name, displayName, qualifiedName, fromSemantic, toSemantic, allowedFromTypes, allowedToTypes );
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
    {
        private ModuleName module;

        private String name;

        private String displayName;

        private DateTime createdTime;

        private DateTime modifiedTime;

        private String fromSemantic;

        private String toSemantic;

        private List<QualifiedContentTypeName> allowedFromTypes = Lists.newArrayList();

        private List<QualifiedContentTypeName> allowedToTypes = Lists.newArrayList();

        private Icon icon;

        private Builder()
        {

        }

        private Builder( final RelationshipType relationshipType )
        {
            this.module = relationshipType.module;
            this.name = relationshipType.name;
            this.displayName = relationshipType.displayName;
            this.createdTime = relationshipType.createdTime;
            this.modifiedTime = relationshipType.modifiedTime;
            this.fromSemantic = relationshipType.fromSemantic;
            this.toSemantic = relationshipType.toSemantic;
            this.allowedFromTypes = Lists.newArrayList( allowedFromTypes );
            this.allowedToTypes = Lists.newArrayList( allowedToTypes );
            this.icon = relationshipType.icon == null ? null : Icon.copyOf( relationshipType.icon );
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

        public Builder displayName( String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder createdTime( DateTime value )
        {
            this.createdTime = value;
            return this;
        }

        public Builder modifiedTime( DateTime value )
        {
            this.modifiedTime = value;
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

        public Builder addAllowedFromType( Iterable<QualifiedContentTypeName> iterable )
        {
            for ( QualifiedContentTypeName contentType : iterable )
            {
                allowedFromTypes.add( contentType );
            }
            return this;
        }

        public Builder addAllowedToType( QualifiedContentTypeName value )
        {
            allowedToTypes.add( value );
            return this;
        }

        public Builder addAllowedToType( Iterable<QualifiedContentTypeName> iterable )
        {
            for ( QualifiedContentTypeName contentType : iterable )
            {
                allowedToTypes.add( contentType );
            }
            return this;
        }

        public Builder icon( Icon icon )
        {
            this.icon = icon;
            return this;
        }

        public RelationshipType build()
        {
            return new RelationshipType( this );
        }
    }
}
