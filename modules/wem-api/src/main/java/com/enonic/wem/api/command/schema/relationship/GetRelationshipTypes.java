package com.enonic.wem.api.command.schema.relationship;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

public final class GetRelationshipTypes
    extends Command<RelationshipTypes>
{
    private RelationshipTypeNames names;

    public RelationshipTypeNames getNames()
    {
        return this.names;
    }

    public GetRelationshipTypes names( final RelationshipTypeNames names )
    {
        this.names = names;
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

        return Objects.equal( this.names, that.names );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.names );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.names, "names cannot be null" );
    }
}
