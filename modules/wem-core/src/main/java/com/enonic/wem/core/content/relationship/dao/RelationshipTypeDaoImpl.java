package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relation.RelationshipType;
import com.enonic.wem.api.content.relation.RelationshipTypes;
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
            throw new SystemException( e, "Unable to create relationship type [{0}]", relationshipType );
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
            throw new SystemException( e, "Unable to update relationship type [{0}]", relationshipType );
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
            throw new SystemException( e, "Unable to delete relationship type [{0}]", relationshipTypeName );
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
            throw new SystemException( e, "Unable to retrieve all relationship types" );
        }
    }

    @Override
    public RelationshipTypes retrieveRelationshipTypes( final QualifiedRelationshipTypeNames relationshipTypeNames, final Session session )
    {
        try
        {
            return new RetrieveRelationshipTypesDaoHandler( session ).handle( relationshipTypeNames );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve relationship types [{0}]", relationshipTypeNames );
        }
    }
}
