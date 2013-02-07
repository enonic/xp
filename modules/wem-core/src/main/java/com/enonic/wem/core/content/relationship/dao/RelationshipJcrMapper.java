package com.enonic.wem.core.content.relationship.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.core.content.relationship.RelationshipJsonSerializer;

class RelationshipJcrMapper
{
    private static final String RELATIONSHIP = "relationship";

    private final RelationshipJsonSerializer jsonSerializer = new RelationshipJsonSerializer().includeId( false );

    void toJcr( final Relationship relationship, final Node relationshipNode )
        throws RepositoryException
    {
        final String relationshipJson = jsonSerializer.toString( relationship );
        relationshipNode.setProperty( RELATIONSHIP, relationshipJson );
    }

    Relationship toRelationship( final Node relationshipNode )
        throws RepositoryException
    {
        final String relationshipJson = relationshipNode.getProperty( RELATIONSHIP ).getString();
        return jsonSerializer.toRelationship( relationshipJson, RelationshipIdFactory.from( relationshipNode ) );
    }
}
