package com.enonic.wem.core.jcr.provider;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class JcrSessionProviderImpl
    implements JcrSessionProvider
{
    private Repository repository;

    @Override
    public Session login()
        throws Exception
    {
        return loginAdmin();
    }

    @Override
    public Session loginAdmin()
        throws Exception
    {
        return this.repository.login( new SimpleCredentials( "admin", "admin".toCharArray() ) );
    }

    @Autowired
    public void setRepository( final Repository repository )
    {
        this.repository = repository;
    }
}
