package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.relationship.RelationshipId;

public final class RelationshipNotFoundException
    extends BaseException
{
    public RelationshipNotFoundException( final RelationshipId id )
    {
        super( "Relationship [{0}] was not found", id );
    }
}
