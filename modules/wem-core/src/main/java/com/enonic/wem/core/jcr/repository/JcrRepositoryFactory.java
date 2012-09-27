package com.enonic.wem.core.jcr.repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jcr.Repository;

import org.apache.jackrabbit.oak.jcr.RepositoryImpl;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public final class JcrRepositoryFactory
    implements FactoryBean<Repository>
{
    // private final static String REPOSITORY_CONFIG = "/META-INF/jcr/repository.xml";

    // private File homeDir;

    // private JackrabbitRepository repository;

    private RepositoryImpl repository;

    @Override
    public Repository getObject()
    {
        return this.repository;
    }

    @Override
    public Class<?> getObjectType()
    {
        return Repository.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

    @PostConstruct
    public void start()
        throws Exception
    {
        this.repository = new RepositoryImpl();
    }

    @PreDestroy
    public void stop()
    {
        // this.repository.shutdown();
    }

    /*
    private JackrabbitRepository createRepository()
        throws Exception
    {
        final RepositoryConfig config = createConfig();
        return RepositoryImpl.create( config );
    }

    private RepositoryConfig createConfig()
        throws Exception
    {
        if ( this.homeDir.exists() )
        {
            FileUtils.deleteQuietly( this.homeDir );
        }

        final URI configUri = getClass().getResource( REPOSITORY_CONFIG ).toURI();
        return RepositoryConfig.create( configUri, this.homeDir.getCanonicalPath() );
    }

    @Value("${cms.home}/jackrabbit")
    public void setHomeDir( final File homeDir )
    {
        this.homeDir = homeDir;
    }*/
}
