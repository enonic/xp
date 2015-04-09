package com.enonic.xp.relationship;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.ContentId;

@Beta
public interface RelationshipService
{
    Relationships getAll( ContentId id );

    RelationshipId create( CreateRelationshipParams params );

    void update( UpdateRelationshipParams params )
        throws RelationshipNotFoundException;

    void delete( RelationshipId id )
        throws RelationshipNotFoundException;
}
