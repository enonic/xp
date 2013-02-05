package com.enonic.wem.core.content.relationship.dao;


import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.core.support.dao.AbstractId;

public class RelationshipIdImpl
    extends AbstractId
    implements RelationshipId
{
    RelationshipIdImpl( final String id )
    {
        super( id );
    }
}
