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

    protected void checkIllegalChange( final String property, final Object previousValue, final Object newValue )
    {
        if ( previousValue == null && newValue == null )
        {
            return;
        }

        if ( previousValue == null )
        {
            throw new IllegalArgumentException( property + " cannot be changed: [" + previousValue + "] -> [" + newValue + "]" );
        }
        else if ( !previousValue.equals( newValue ) )
        {
            throw new IllegalArgumentException( property + " cannot be changed: [" + previousValue + "] -> [" + newValue + "]" );
        }
    }
}
