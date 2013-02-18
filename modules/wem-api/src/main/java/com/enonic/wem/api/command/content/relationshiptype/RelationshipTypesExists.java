package com.enonic.wem.api.command.content.relationshiptype;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeNames;

public final class RelationshipTypesExists
    extends Command<RelationshipTypesExistsResult>
{
    private QualifiedRelationshipTypeNames qualifiedNames;

    public QualifiedRelationshipTypeNames getQualifiedNames()
    {
        return this.qualifiedNames;
    }

    public RelationshipTypesExists qualifiedNames( final QualifiedRelationshipTypeNames qualifiedNames )
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

        if ( !( o instanceof RelationshipTypesExists ) )
        {
            return false;
        }

        final RelationshipTypesExists that = (RelationshipTypesExists) o;
        return Objects.equal( this.qualifiedNames, that.qualifiedNames );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( qualifiedNames );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.qualifiedNames, "qualifiedNames cannot be null" );
    }

}
