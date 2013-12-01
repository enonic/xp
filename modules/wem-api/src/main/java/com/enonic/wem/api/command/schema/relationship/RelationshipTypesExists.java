package com.enonic.wem.api.command.schema.relationship;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;

public final class RelationshipTypesExists
    extends Command<RelationshipTypesExistsResult>
{
    private RelationshipTypeNames names;

    public RelationshipTypeNames getNames()
    {
        return this.names;
    }

    public RelationshipTypesExists names( final RelationshipTypeNames names )
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

        if ( !( o instanceof RelationshipTypesExists ) )
        {
            return false;
        }

        final RelationshipTypesExists that = (RelationshipTypesExists) o;
        return Objects.equal( this.names, that.names );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( names );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.names, "names cannot be null" );
    }

}
