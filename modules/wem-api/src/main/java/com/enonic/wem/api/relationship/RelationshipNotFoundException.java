package com.enonic.wem.api.relationship;

import java.text.MessageFormat;

public final class RelationshipNotFoundException
    extends RuntimeException
{
    public RelationshipNotFoundException( final RelationshipId id )
    {
        super( MessageFormat.format( "Relationship [{0}] was not found", id ) );
    }

    public RelationshipNotFoundException( final RelationshipKey key )
    {
        super( MessageFormat.format( "Relationship [{0}] was not found", key ) );
    }
}
