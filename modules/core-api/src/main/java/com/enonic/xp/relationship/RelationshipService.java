package com.enonic.xp.relationship;

import com.enonic.xp.content.ContentId;

public interface RelationshipService
{
    public Relationships getAll( ContentId id );

    public RelationshipId create( CreateRelationshipParams params );

    public void update( UpdateRelationshipParams params )
        throws RelationshipNotFoundException;

    public void delete( RelationshipId id )
        throws RelationshipNotFoundException;
}
