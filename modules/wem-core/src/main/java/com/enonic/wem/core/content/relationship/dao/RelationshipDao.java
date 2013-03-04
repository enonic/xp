package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.Session;

import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.relationship.RelationshipIds;
import com.enonic.wem.api.content.relationship.RelationshipKey;
import com.enonic.wem.api.exception.RelationshipNotFoundException;


public interface RelationshipDao
{
    public static final String RELATIONSHIPS_NODE = "relationships";

    public RelationshipId create( final Relationship relationship, final Session session );

    public void update( final Relationship relationship, final Session session )
        throws RelationshipNotFoundException;

    public void delete( final RelationshipId relationshipId, final Session session );

    public void delete( final RelationshipKey relationshipKey, final Session session );

    public RelationshipIds exists( final RelationshipIds relationshipIds, final Session session );

    public Relationship select( RelationshipKey relationshipKey, final Session session );

}
