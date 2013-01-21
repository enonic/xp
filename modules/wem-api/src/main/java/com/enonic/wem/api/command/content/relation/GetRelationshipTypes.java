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

    private boolean getAllContentTypes = false;

    public QualifiedRelationshipTypeNames getNames()
    {
        return this.relationshipTypeNames;
    }

    public GetRelationshipTypes names( final QualifiedRelationshipTypeNames qualifiedNames )
    {
        this.relationshipTypeNames = qualifiedNames;
        return this;
    }

    public boolean isGetAll()
    {
        return getAllContentTypes;
    }

    public GetRelationshipTypes all()
    {
        getAllContentTypes = true;
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
        return Objects.equal( this.relationshipTypeNames, that.relationshipTypeNames ) &&
            ( this.getAllContentTypes == that.getAllContentTypes );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.relationshipTypeNames, this.getAllContentTypes );
    }

    @Override
    public void validate()
    {
        if ( getAllContentTypes )
        {
            Preconditions.checkArgument( this.relationshipTypeNames == null,
                                         "Cannot specify both get all and get relationship type names" );
        }
        else
        {
            Preconditions.checkNotNull( this.relationshipTypeNames, "Relationship type cannot be null" );
        }
    }

}
