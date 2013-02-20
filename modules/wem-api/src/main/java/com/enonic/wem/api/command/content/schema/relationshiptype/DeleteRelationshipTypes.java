package com.enonic.wem.api.command.content.schema.relationshiptype;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.schema.relationshiptype.QualifiedRelationshipTypeNames;

public final class DeleteRelationshipTypes
    extends Command<RelationshipTypeDeletionResult>
{
    private QualifiedRelationshipTypeNames qualifiedNames;

    public QualifiedRelationshipTypeNames getQualifiedNames()
    {
        return this.qualifiedNames;
    }

    public DeleteRelationshipTypes qualifiedNames( final QualifiedRelationshipTypeNames qualifiedNames )
    {
        this.qualifiedNames = qualifiedNames;
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
        return Objects.equal( this.qualifiedNames, that.qualifiedNames );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.qualifiedNames );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.qualifiedNames, "Relationship type names cannot be null" );
    }
}
