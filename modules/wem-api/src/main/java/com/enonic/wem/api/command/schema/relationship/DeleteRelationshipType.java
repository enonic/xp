package com.enonic.wem.api.command.schema.relationship;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public final class DeleteRelationshipType
    extends Command<DeleteRelationshipTypeResult>
{
    private RelationshipTypeName qualifiedName;

    public RelationshipTypeName getQualifiedName()
    {
        return this.qualifiedName;
    }

    public DeleteRelationshipType qualifiedName( final RelationshipTypeName qualifiedNames )
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
