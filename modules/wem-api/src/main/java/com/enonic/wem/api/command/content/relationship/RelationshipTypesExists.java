package com.enonic.wem.api.command.content.relationship;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.relationship.RelationshipTypeSelectors;

public final class RelationshipTypesExists
    extends Command<RelationshipTypesExistsResult>
{
    private RelationshipTypeSelectors selectors;

    public RelationshipTypeSelectors getSelectors()
    {
        return this.selectors;
    }

    public RelationshipTypesExists selectors( final RelationshipTypeSelectors selectors )
    {
        this.selectors = selectors;
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
        return Objects.equal( this.selectors, that.selectors );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( selectors );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.selectors, "selectors cannot be null" );
    }

}
