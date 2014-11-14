package com.enonic.wem.core.entity;


import java.time.Instant;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.Name;
import com.enonic.wem.api.content.thumb.Thumbnail;
import com.enonic.wem.api.support.illegaledit.IllegalEdit;
import com.enonic.wem.api.support.illegaledit.IllegalEditAware;

public final class RelationshipType
    implements IllegalEditAware<RelationshipType>
{
    private final Name name;

    private final String displayName;

    private final Instant createdTime;

    private final Instant modifiedTime;

    private final RelationshipTypeName relationshipTypeName;

    private final String fromSemantic;

    private final String toSemantic;

    //private final ContentTypeNames allowedFromTypes;

    //private final ContentTypeNames allowedToTypes;

    private final Thumbnail thumbnail;

    private RelationshipType( final Builder builder )
    {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.relationshipTypeName = new RelationshipTypeName( builder.name );
        this.fromSemantic = builder.fromSemantic;
        this.toSemantic = builder.toSemantic;
        this.thumbnail = builder.thumbnail;
    }

    public String getName()
    {
        return name != null ? name.toString() : null;
    }

    public RelationshipTypeName getRelationshipTypeName()
    {
        return relationshipTypeName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Instant getCreatedTime()
    {
        return createdTime;
    }

    public Instant getModifiedTime()
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

    public Thumbnail getThumbnail()
    {
        return thumbnail;
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
            Objects.equal( this.relationshipTypeName, that.relationshipTypeName ) &&
            Objects.equal( this.fromSemantic, that.fromSemantic ) &&
            Objects.equal( this.toSemantic, that.toSemantic );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( name, displayName, relationshipTypeName, fromSemantic, toSemantic );
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

        private Instant createdTime;

        private Instant modifiedTime;

        private String fromSemantic;

        private String toSemantic;

        private Thumbnail thumbnail;

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
            this.thumbnail = relationshipType.thumbnail;
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

        public Builder createdTime( Instant value )
        {
            this.createdTime = value;
            return this;
        }

        public Builder modifiedTime( Instant value )
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

        public Builder icon( Thumbnail thumbnail )
        {
            this.thumbnail = thumbnail;
            return this;
        }

        public RelationshipType build()
        {
            return new RelationshipType( this );
        }
    }
}
