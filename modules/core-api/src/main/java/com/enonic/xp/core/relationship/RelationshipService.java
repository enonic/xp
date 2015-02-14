package com.enonic.xp.core.relationship;

import com.enonic.xp.core.content.ContentId;

public interface RelationshipService
{
    public Relationships getAll( ContentId id );

    public RelationshipId create( CreateRelationshipParams params );

    public void update( UpdateRelationshipParams params )
        throws RelationshipNotFoundException;

    public void delete( RelationshipId id )
        throws RelationshipNotFoundException;
}
