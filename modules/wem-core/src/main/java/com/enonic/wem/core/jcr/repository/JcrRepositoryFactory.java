package com.enonic.wem.core.jcr.repository;

import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.jcr.Repository;

import org.apache.jackrabbit.mk.api.MicroKernel;
import org.apache.jackrabbit.oak.jcr.RepositoryImpl;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class JcrRepositoryFactory
    implements FactoryBean<Repository>
{
    private MicroKernel microKernel;

    private Repository repository;

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
    public void init()
    {
        this.repository = new RepositoryImpl( this.microKernel, Executors.newSingleThreadScheduledExecutor() );
        /*

        final Oak oak = new Oak( this.microKernel );
        oak.with( new InitialContent() );
        oak.with( new DefaultTypeEditor() );
        oak.with( new NameValidatorProvider() );
        oak.with( new NamespaceValidatorProvider() );
        oak.with( new TypeValidatorProvider() );
        oak.with( new RegistrationValidatorProvider() );
        oak.with( new ConflictValidatorProvider() );
        oak.with( new AnnotatingConflictHandler() );
        oak.with( new PropertyIndexProvider() );
        oak.with( new LuceneIndexProvider() );
        oak.with(
            new IndexHookManager( new CompositeIndexHookProvider( new PropertyIndexHookProvider(), new LuceneIndexHookProvider() ) ) );
        this.repository = new RepositoryImpl( oak.createContentRepository(), null, null );*/
    }

    @Autowired
    public void setMicroKernel( final MicroKernel microKernel )
    {
        this.microKernel = microKernel;
    }
}