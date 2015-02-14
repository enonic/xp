package com.enonic.xp.core.relationship;

import java.text.MessageFormat;

import com.google.common.base.Joiner;

import com.enonic.xp.core.exception.NotFoundException;

public final class RelationshipNotFoundException
    extends NotFoundException
{
    public RelationshipNotFoundException( final RelationshipId id )
    {
        super( MessageFormat.format( "Relationship [{0}] was not found", id ) );
    }

    public RelationshipNotFoundException( final RelationshipKey key )
    {
        super( MessageFormat.format( "Relationship [{0}] was not found", key ) );
    }

    public RelationshipNotFoundException( final Relationships relationships )
    {
        super( MessageFormat.format( "Relationships [{0}] were not found", Joiner.on( ", " ).join( relationships ) ) );
    }
}
