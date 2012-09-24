package com.enonic.wem.core.jcr.old;

import javax.annotation.PostConstruct;
import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SessionFactoryImpl
        implements SessionFactory
{
    private Repository repository;

    private String userId;

    private String password;

    private Credentials credentials;

    public SessionFactoryImpl()
    {
    }

    @PostConstruct
    private void initialize()
    {
        credentials = new SimpleCredentials( userId, password.toCharArray() );
    }

    @Override
    public JcrSession getSession()
    {
        try
        {
            final Session session = repository.login( credentials );
            final JcrRepository jcrRepo = new JcrRepositoryImpl( repository );
            return new JcrSessionImpl(session, jcrRepo);
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public void releaseSession( JcrSession session )
    {
        session.logout();
    }

    @Autowired
    public void setRepository( Repository repository )
    {
        this.repository = repository;
    }

    @Value("admin")
    public void setUserId( String userId )
    {
        this.userId = userId;
    }

    @Value("admin")
    public void setPassword( String password )
    {
        this.password = password;
    }

}
