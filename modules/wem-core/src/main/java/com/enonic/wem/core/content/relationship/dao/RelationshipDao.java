package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.Session;

import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.relationship.RelationshipIds;
import com.enonic.wem.api.content.relationship.RelationshipSelectors;
import com.enonic.wem.api.content.relationship.Relationships;


public interface RelationshipDao
{
    public static final String RELATIONSHIPS_NODE = "relationships";

    public RelationshipId create( final Relationship relationship, final Session session );

    public void update( final Relationship relationship, final Session session );

    public void delete( final RelationshipId relationshipId, final Session session );

    public RelationshipIds exists( final RelationshipIds relationshipIds, final Session session );

    public Relationships select( RelationshipSelectors selectors, final Session session );
}
