package com.enonic.wem.api.schema.relationship;

import com.enonic.wem.api.exception.BaseException;

public final class RelationshipTypeAlreadyExistException
    extends BaseException
{
    public RelationshipTypeAlreadyExistException( final RelationshipTypeName name )
    {
        super( "RelationshipType [{0}] already exist", name );
    }
}
