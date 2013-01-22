package com.enonic.wem.core.content.relationship.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.content.relation.RelationshipType;
import com.enonic.wem.core.content.relationship.RelationshipTypeJsonSerializer;

class RelationshipTypeJcrMapper
{
    private static final String RELATIONSHIP_TYPE = "relationshipType";

    private RelationshipTypeJsonSerializer jsonSerializer = new RelationshipTypeJsonSerializer();

    void toJcr( final RelationshipType relationshipType, final Node relationshipTypeNode )
        throws RepositoryException
    {
        final String relationshipTypeJson = jsonSerializer.toString( relationshipType );
        relationshipTypeNode.setProperty( RELATIONSHIP_TYPE, relationshipTypeJson );
    }

    RelationshipType toRelationshipType( final Node relationshipTypeNode )
        throws RepositoryException
    {
        final String relationshipTypeJson = relationshipTypeNode.getProperty( RELATIONSHIP_TYPE ).getString();
        return jsonSerializer.toObject( relationshipTypeJson );
    }

}
