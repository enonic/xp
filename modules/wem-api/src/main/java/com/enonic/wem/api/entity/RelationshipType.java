package com.enonic.wem.api.entity;


import org.joda.time.DateTime;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.Name;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.support.illegaledit.IllegalEdit;
import com.enonic.wem.api.support.illegaledit.IllegalEditAware;

public final class RelationshipType
    implements IllegalEditAware<RelationshipType>
{
    private final Name name;

    private final String displayName;

    private final DateTime createdTime;

    private final DateTime modifiedTime;

    private final QualifiedRelationshipTypeName qualifiedName;

    private final String fromSemantic;

    private final String toSemantic;

    //private final ContentTypeNames allowedFromTypes;

    //private final ContentTypeNames allowedToTypes;

    private final Icon icon;

    private RelationshipType( final Builder builder )
    {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.qualifiedName = new QualifiedRelationshipTypeName( builder.name );
        this.fromSemantic = builder.fromSemantic;
        this.toSemantic = builder.toSemantic;
        this.icon = builder.icon;
    }

    public String getName()
    {
        return name != null ? name.toString() : null;
    }

    public QualifiedRelationshipTypeName getQualifiedName()
    {
        return qualifiedName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public DateTime getCreatedTime()
    {
        return createdTime;
    }

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
        return Objects.equal( this.name, that.name ) &&
            Objects.equal( this.displayName, that.displayName ) &&
            Objects.equal( this.qualifiedName, that.qualifiedName ) &&
            Objects.equal( this.fromSemantic, that.fromSemantic ) &&
            Objects.equal( this.toSemantic, that.toSemantic );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( name, displayName, qualifiedName, fromSemantic, toSemantic );
    }

    public void checkIllegalEdit( final RelationshipType to )
    {
        Preconditions.checkArgument( this.getCreatedTime().equals( to.getCreatedTime() ) );
        IllegalEdit.check( "createdTime", this.getCreatedTime(), to.getCreatedTime(), RelationshipType.class );
        IllegalEdit.check( "modifiedTime", this.getModifiedTime(), to.getModifiedTime(), RelationshipType.class );

        // Cannot be changed since they are a part of a Relationship's storage path in JCR.
        IllegalEdit.check( "name", this.getName(), to.getName(), RelationshipType.class );
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
        private Name name;

        private String displayName;

        private DateTime createdTime;

        private DateTime modifiedTime;

        private String fromSemantic;

        private String toSemantic;

        private Icon icon;

        private Builder()
        {

        }

        private Builder( final RelationshipType relationshipType )
        {
            this.name = relationshipType.name;
            this.displayName = relationshipType.displayName;
            this.createdTime = relationshipType.createdTime;
            this.modifiedTime = relationshipType.modifiedTime;
            this.fromSemantic = relationshipType.fromSemantic;
            this.toSemantic = relationshipType.toSemantic;
            this.icon = relationshipType.icon;
        }

        public Builder name( String value )
        {
            this.name = value != null ? Name.from( value ) : null;
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
