package com.enonic.wem.core.content.type.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.QualifiedSubTypeNames;
import com.enonic.wem.api.content.type.SubTypes;
import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.api.exception.SystemException;

@Component
public class SubTypeDaoImpl
    implements SubTypeDao
{

    @Override
    public void createSubType( final SubType subType, final Session session )
    {
        try
        {
            new CreateSubTypeDaoHandler( session ).create( subType );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to create sub type [{0}]", subType );
        }
    }

    @Override
    public void updateSubType( final SubType subType, final Session session )
    {
        try
        {
            new UpdateSubTypeDaoHandler( session ).update( subType );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to update sub type [{0}]", subType );
        }
    }

    @Override
    public void deleteSubType( final QualifiedSubTypeName qualifiedSubTypeName, final Session session )
    {
        try
        {
            new DeleteSubTypeDaoHandler( session ).handle( qualifiedSubTypeName );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to delete sub type [{0}]", qualifiedSubTypeName );
        }
    }

    @Override
    public SubTypes retrieveAllSubTypes( final Session session )
    {
        try
        {
            return new RetrieveSubTypeDaoHandler( session ).retrieveAll();
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve all sub types" );
        }
    }

    @Override
    public SubTypes retrieveSubTypes( final QualifiedSubTypeNames qualifiedSubTypeNames, Session session )
    {
        try
        {
            return new RetrieveSubTypeDaoHandler( session ).retrieve( qualifiedSubTypeNames );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve sub types [{0}]", qualifiedSubTypeNames );
        }
    }
}
