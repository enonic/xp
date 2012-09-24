package com.enonic.wem.core.jcr.old;

import java.io.IOException;

import javax.jcr.RepositoryException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class JcrTemplate
{
    private SessionFactory sessionFactory;

    public JcrTemplate( )
    {
    }

    public Object execute( JcrCallback action )
    {
        final JcrSession session = sessionFactory.getSession();

        try
        {
            Object result = action.doInJcr( session );
            return result;
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e.getMessage(), e );
        }
        catch ( IOException e )
        {
            throw new RepositoryRuntimeException( e.getMessage(), e );
        }
        finally
        {
            sessionFactory.releaseSession( session );
        }
    }

    @Autowired
    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
}
