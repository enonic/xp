package com.enonic.wem.core.support.dao;


import javax.jcr.RepositoryException;

import com.enonic.wem.api.exception.SystemException;

public abstract class AbstractDaoHandler<R>
{
    private R result;

    public final AbstractDaoHandler handle()
    {
        try
        {
            doHandle();
            return this;
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Failed to handle " + getClass().getSimpleName() );
        }
    }

    protected abstract void doHandle()
        throws RepositoryException;


    protected void setResult( final R result )
    {
        this.result = result;
    }

    public R getResult()
    {
        return result;
    }
}
