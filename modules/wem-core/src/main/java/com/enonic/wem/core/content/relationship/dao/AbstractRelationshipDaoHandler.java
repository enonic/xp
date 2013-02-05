package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.core.support.dao.AbstractDaoHandler;


abstract class AbstractRelationshipDaoHandler<T>
    extends AbstractDaoHandler<T>
{
    protected final Session session;

    protected final RelationshipJcrMapper relationshipJcrMapper = new RelationshipJcrMapper();

    AbstractRelationshipDaoHandler( final Session session )
    {
        this.session = session;
    }

    protected final Node getRelationshipNode( final RelationshipId relationshipId )
        throws RepositoryException
    {
        return session.getNodeByIdentifier( relationshipId.toString() );
    }

    protected final boolean relationshipExists( final RelationshipId relationshipId )
        throws RepositoryException
    {
        return session.getNodeByIdentifier( relationshipId.toString() ) != null;
    }
}
