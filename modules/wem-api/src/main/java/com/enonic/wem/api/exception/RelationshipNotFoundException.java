package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.relationship.RelationshipKey;

public final class RelationshipNotFoundException
    extends BaseException
{
    public RelationshipNotFoundException( final RelationshipId id )
    {
        super( "Relationship [{0}] was not found", id );
    }

    public RelationshipNotFoundException( final RelationshipKey key )
    {
        super( "Relationship [{0}] was not found", key );
    }
}
