package com.enonic.wem.core.support.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.exception.SystemException;

public abstract class AbstractCrudDao<TObject, TObjects, TQualifiedName, TQualifiedNames, TSelectors>
    implements CrudDao<TObject, TObjects, TQualifiedName, TQualifiedNames, TSelectors>
{
    private Class clazz;

    protected AbstractCrudDao( final Class clazz )
    {
        this.clazz = clazz;
    }

    @Override
    public void create( final TObject object, final Session session )
    {
        try
        {
            doCreate( object, session );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to create {0} [{1}]", clazz.getSimpleName(), object );
        }
    }

    protected abstract void doCreate( final TObject object, final Session session )
        throws RepositoryException;

    @Override
    public void update( final TObject object, final Session session )
    {
        try
        {
            doUpdate( object, session );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to update {0} [{1}]", clazz.getSimpleName(), object );
        }
    }

    protected abstract void doUpdate( final TObject object, final Session session )
        throws RepositoryException;

    @Override
    public void delete( final TQualifiedName qualifiedName, final Session session )
    {
        try
        {
            doDelete( qualifiedName, session );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to delete {0} [{1}]", clazz.getSimpleName(), qualifiedName );
        }
    }

    protected abstract void doDelete( final TQualifiedName qualifiedName, final Session session )
        throws RepositoryException;


    @Override
    public TQualifiedNames exists( final TSelectors selectors, final Session session )
    {
        try
        {
            return doExists( selectors, session );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to check {0} [{1}]", clazz.getSimpleName(), selectors );
        }
    }

    protected abstract TQualifiedNames doExists( final TSelectors selectors, final Session session )
        throws RepositoryException;

    @Override
    public TObjects selectAll( final Session session )
    {
        try
        {
            return doSelectAll( session );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to select all {}s", clazz.getSimpleName() );
        }
    }

    protected abstract TObjects doSelectAll( final Session session )
        throws RepositoryException;

    @Override
    public TObjects select( final TSelectors selectors, final Session session )
    {
        try
        {
            return doSelect( selectors, session );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to select {0} [{1}]", clazz.getSimpleName(), selectors );
        }
    }

    protected abstract TObjects doSelect( final TSelectors selectors, final Session session )
        throws RepositoryException;
}
