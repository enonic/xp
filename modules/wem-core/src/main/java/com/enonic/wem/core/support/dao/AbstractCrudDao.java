package com.enonic.wem.core.support.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.exception.SystemException;

public abstract class AbstractCrudDao<T, Ts, QN, QNs, Ss>
    implements CrudDao<T, Ts, QN, QNs, Ss>
{
    private Class clazz;

    protected AbstractCrudDao( final Class clazz )
    {
        this.clazz = clazz;
    }

    @Override
    public void create( final T object, final Session session )
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

    protected abstract void doCreate( final T object, final Session session )
        throws RepositoryException;

    @Override
    public void update( final T object, final Session session )
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

    protected abstract void doUpdate( final T object, final Session session )
        throws RepositoryException;

    @Override
    public void delete( final QN qualifiedName, final Session session )
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

    protected abstract void doDelete( final QN qualifiedName, final Session session )
        throws RepositoryException;


    @Override
    public QNs exists( final Ss selectors, final Session session )
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

    protected abstract QNs doExists( final Ss selectors, final Session session )
        throws RepositoryException;

    @Override
    public Ts selectAll( final Session session )
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

    protected abstract Ts doSelectAll( final Session session )
        throws RepositoryException;

    @Override
    public Ts select( final Ss selectors, final Session session )
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

    protected abstract Ts doSelect( final Ss selectors, final Session session )
        throws RepositoryException;
}
