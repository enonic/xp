package com.enonic.wem.api.command.content.relationship;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeNames;

public final class DeleteRelationshipTypes
    extends Command<RelationshipTypeDeletionResult>
{
    private QualifiedRelationshipTypeNames relationshipTypeNames;

    public QualifiedRelationshipTypeNames getNames()
    {
        return this.relationshipTypeNames;
    }

    public DeleteRelationshipTypes names( final QualifiedRelationshipTypeNames relationshipTypeNames )
    {
        this.relationshipTypeNames = relationshipTypeNames;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof DeleteRelationshipTypes ) )
        {
            return false;
        }

        final DeleteRelationshipTypes that = (DeleteRelationshipTypes) o;
        return Objects.equal( this.relationshipTypeNames, that.relationshipTypeNames );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.relationshipTypeNames );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.relationshipTypeNames, "Relationship type names cannot be null" );
    }
}
