package com.enonic.wem.api.command.content.relationship;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.relationship.RelationshipTypeSelectors;
import com.enonic.wem.api.content.relationship.RelationshipTypes;

public final class GetRelationshipTypes
    extends Command<RelationshipTypes>
{
    private RelationshipTypeSelectors selectors;

    private boolean all = false;

    public RelationshipTypeSelectors getSelectors()
    {
        return this.selectors;
    }

    public GetRelationshipTypes selectors( final RelationshipTypeSelectors selectors )
    {
        this.selectors = selectors;
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
        return Objects.equal( this.selectors, that.selectors ) && ( this.all == that.all );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.selectors, this.all );
    }

    @Override
    public void validate()
    {
        if ( all )
        {
            Preconditions.checkArgument( this.selectors == null, "all cannot be true at the same time as selectors is specified" );
        }
        else
        {
            Preconditions.checkNotNull( this.selectors, "selectors cannot be null" );
        }
    }

}
