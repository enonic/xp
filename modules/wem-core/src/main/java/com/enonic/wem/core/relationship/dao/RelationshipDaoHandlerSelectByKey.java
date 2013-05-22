package com.enonic.wem.core.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.relationship.RelationshipKey;


final class RelationshipDaoHandlerSelectByKey
    extends AbstractRelationshipDaoHandler<Relationship>
{
    private RelationshipKey relationshipKey;


    RelationshipDaoHandlerSelectByKey( final Session session )
    {
        super( session );

    }

    RelationshipDaoHandlerSelectByKey relationshipKey( final RelationshipKey relationshipKey )
    {
        this.relationshipKey = relationshipKey;
        return this;
    }

    protected void doHandle()
        throws RepositoryException
    {
        final Node node = getRelationshipNode( relationshipKey );
        final Relationship relationship = relationshipJcrMapper.toRelationship( node );

        setResult( relationship );
    }

}
