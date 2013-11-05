package com.enonic.wem.api.exception;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public final class RelationshipTypeNotFoundException
    extends BaseException
{
    public RelationshipTypeNotFoundException( final RelationshipTypeName qName )
    {
        super( "RelationshipType [{0}] was not found", qName );
    }
}
