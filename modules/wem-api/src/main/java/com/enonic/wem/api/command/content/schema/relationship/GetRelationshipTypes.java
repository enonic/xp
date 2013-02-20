package com.enonic.wem.api.command.content.schema.relationship;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.schema.relationship.RelationshipTypes;

public final class GetRelationshipTypes
    extends Command<RelationshipTypes>
{
    private QualifiedRelationshipTypeNames qualifiedNames;

    private boolean all = false;

    public QualifiedRelationshipTypeNames getQualifiedNames()
    {
        return this.qualifiedNames;
    }

    public GetRelationshipTypes qualifiedNames( final QualifiedRelationshipTypeNames qualifiedNames )
    {
        this.qualifiedNames = qualifiedNames;
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
        return Objects.equal( this.qualifiedNames, that.qualifiedNames ) && ( this.all == that.all );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.qualifiedNames, this.all );
    }

    @Override
    public void validate()
    {
        if ( all )
        {
            Preconditions.checkArgument( this.qualifiedNames == null,
                                         "all cannot be true at the same time as qualifiedNames is specified" );
        }
        else
        {
            Preconditions.checkNotNull( this.qualifiedNames, "qualifiedNames cannot be null" );
        }
    }

}
