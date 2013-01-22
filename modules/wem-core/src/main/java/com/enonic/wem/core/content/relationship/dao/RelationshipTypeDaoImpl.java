package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relationship.RelationshipType;
import com.enonic.wem.api.content.relationship.RelationshipTypeSelector;
import com.enonic.wem.api.content.relationship.RelationshipTypes;
import com.enonic.wem.api.exception.SystemException;

@Component
public final class RelationshipTypeDaoImpl
    implements RelationshipTypeDao
{

    @Override
    public void createRelationshipType( final RelationshipType relationshipType, final Session session )
    {
        try
        {
            new CreateRelationshipTypeDaoHandler( session ).handle( relationshipType );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to create RelationshipType [{0}]", relationshipType );
        }
    }

    @Override
    public void updateRelationshipType( final RelationshipType relationshipType, final Session session )
    {
        try
        {
            new UpdateRelationshipTypeDaoHandler( session ).handle( relationshipType );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to update RelationshipType [{0}]", relationshipType );
        }
    }

    @Override
    public void deleteRelationshipType( final QualifiedRelationshipTypeName relationshipTypeName, final Session session )
    {
        try
        {
            new DeleteRelationshipTypeDaoHandler( session ).handle( relationshipTypeName );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to delete RelationshipType [{0}]", relationshipTypeName );
        }
    }

    @Override
    public RelationshipTypes retrieveAllRelationshipTypes( final Session session )
    {
        try
        {
            return new RetrieveRelationshipTypesDaoHandler( session ).handle();
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve all RelationshipTypes" );
        }
    }

    @Override
    public RelationshipTypes retrieveRelationshipTypes( final RelationshipTypeSelector relationshipTypeNames, final Session session )
    {
        try
        {
            return new RetrieveRelationshipTypesDaoHandler( session ).handle( relationshipTypeNames );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve RelationshipTypes [{0}]", relationshipTypeNames );
        }
    }

    @Override
    public QualifiedRelationshipTypeNames exists( final RelationshipTypeSelector selector, final Session session )
    {
        try
        {
            return new RelationshipTypesExistsDaoHandler( session ).handle( selector );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to check RelationshipTypes [{0}]", selector );
        }
    }
}
