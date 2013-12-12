package com.enonic.wem.api.exception;

import com.enonic.wem.api.NotFoundException;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public final class RelationshipTypeNotFoundException
    extends NotFoundException
{
    public RelationshipTypeNotFoundException( final RelationshipTypeName name )
    {
        super( "RelationshipType [{0}] was not found", name );
    }
}
