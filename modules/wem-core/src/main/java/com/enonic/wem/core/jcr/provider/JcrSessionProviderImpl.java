package com.enonic.wem.core.jcr.provider;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import javax.inject.Inject;
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

    @Inject
    public void setRepository( final Repository repository )
    {
        this.repository = repository;
    }
}
