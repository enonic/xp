package com.enonic.wem.api.command.content.relation;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.relation.RelationshipTypeSelector;

public final class RelationshipTypesExists
    extends Command<RelationshipTypesExistsResult>
{
    private RelationshipTypeSelector selector;

    public RelationshipTypeSelector getSelector()
    {
        return this.selector;
    }

    public RelationshipTypesExists selector( final RelationshipTypeSelector selector )
    {
        this.selector = selector;
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
        return Objects.equal( this.selector, that.selector );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( selector );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.selector, "selector cannot be null" );
    }

}
