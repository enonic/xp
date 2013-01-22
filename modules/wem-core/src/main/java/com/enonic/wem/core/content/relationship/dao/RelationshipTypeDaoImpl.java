package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relationship.RelationshipType;
import com.enonic.wem.api.content.relationship.RelationshipTypeSelectors;
import com.enonic.wem.api.content.relationship.RelationshipTypes;
import com.enonic.wem.core.support.dao.AbstractCrudDao;

@Component
public final class RelationshipTypeDaoImpl
    extends
    AbstractCrudDao<RelationshipType, RelationshipTypes, QualifiedRelationshipTypeName, QualifiedRelationshipTypeNames, RelationshipTypeSelectors>
    implements RelationshipTypeDao
{

    public RelationshipTypeDaoImpl()
    {
        super( RelationshipType.class );
    }

    @Override
    protected void doCreate( final RelationshipType relationshipType, final Session session )
        throws RepositoryException
    {
        new CreateRelationshipTypeDaoHandler( session ).handle( relationshipType );
    }

    @Override
    protected void doUpdate( final RelationshipType relationshipType, final Session session )
        throws RepositoryException
    {
        new UpdateRelationshipTypeDaoHandler( session ).handle( relationshipType );
    }

    @Override
    protected void doDelete( final QualifiedRelationshipTypeName relationshipTypeName, final Session session )
        throws RepositoryException
    {
        new DeleteRelationshipTypeDaoHandler( session ).handle( relationshipTypeName );
    }

    @Override
    protected QualifiedRelationshipTypeNames doExists( final RelationshipTypeSelectors selectors, final Session session )
        throws RepositoryException
    {
        return new RelationshipTypesExistsDaoHandler( session ).handle( selectors );
    }

    @Override
    protected RelationshipTypes doSelectAll( final Session session )
        throws RepositoryException
    {
        return new RetrieveRelationshipTypesDaoHandler( session ).handle();
    }

    @Override
    protected RelationshipTypes doSelect( final RelationshipTypeSelectors selectors, final Session session )
        throws RepositoryException
    {
        return new RetrieveRelationshipTypesDaoHandler( session ).handle( selectors );
    }
}
