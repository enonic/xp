package com.enonic.wem.api.command.content.relation;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relation.RelationshipTypes;

public final class GetRelationshipTypes
    extends Command<RelationshipTypes>
{
    private QualifiedRelationshipTypeNames relationshipTypeNames;

    private boolean all = false;

    public QualifiedRelationshipTypeNames getQualifiedNames()
    {
        return this.relationshipTypeNames;
    }

    public GetRelationshipTypes qualifiedNames( final QualifiedRelationshipTypeNames qualifiedNames )
    {
        this.relationshipTypeNames = qualifiedNames;
        return this;
    }

    public boolean isGetAll()
    {
        return all;
    }

    public GetRelationshipTypes all()
    {
        all = true;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetRelationshipTypes ) )
        {
            return false;
        }

        final GetRelationshipTypes that = (GetRelationshipTypes) o;
        return Objects.equal( this.relationshipTypeNames, that.relationshipTypeNames ) && ( this.all == that.all );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.relationshipTypeNames, this.all );
    }

    @Override
    public void validate()
    {
        if ( all )
        {
            Preconditions.checkArgument( this.relationshipTypeNames == null,
                                         "all and relationshipTypeNames cannot be specified at the same time" );
        }
        else
        {
            Preconditions.checkNotNull( this.relationshipTypeNames, "RelationshipType cannot be null" );
        }
    }

}
