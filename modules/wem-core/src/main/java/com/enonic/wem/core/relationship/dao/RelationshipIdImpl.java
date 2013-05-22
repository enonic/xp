package com.enonic.wem.core.relationship.dao;


import com.enonic.wem.api.relationship.RelationshipId;
import com.enonic.wem.api.support.AbstractId;

public class RelationshipIdImpl
    extends AbstractId
    implements RelationshipId
{
    RelationshipIdImpl( final String id )
    {
        super( id );
    }
}
