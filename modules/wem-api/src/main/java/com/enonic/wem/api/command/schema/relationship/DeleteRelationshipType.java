package com.enonic.wem.api.command.schema.relationship;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

public final class DeleteRelationshipType
    extends Command<DeleteRelationshipTypeResult>
{
    private QualifiedRelationshipTypeName qualifiedName;

    public QualifiedRelationshipTypeName getQualifiedName()
    {
        return this.qualifiedName;
    }

    public DeleteRelationshipType qualifiedName( final QualifiedRelationshipTypeName qualifiedNames )
    {
        this.qualifiedName = qualifiedNames;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof DeleteRelationshipType ) )
        {
            return false;
        }

        final DeleteRelationshipType that = (DeleteRelationshipType) o;
        return Objects.equal( this.qualifiedName, that.qualifiedName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.qualifiedName );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.qualifiedName, "Relationship type name cannot be null" );
    }
}
