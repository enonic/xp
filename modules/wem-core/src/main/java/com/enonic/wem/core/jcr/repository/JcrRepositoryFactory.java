package com.enonic.wem.core.jcr.repository;

import javax.annotation.PostConstruct;
import javax.jcr.Repository;

import org.apache.jackrabbit.mk.api.MicroKernel;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.RepositoryImpl;
import org.apache.jackrabbit.oak.plugins.commit.AnnotatingConflictHandler;
import org.apache.jackrabbit.oak.plugins.commit.ConflictValidatorProvider;
import org.apache.jackrabbit.oak.plugins.index.CompositeIndexHookProvider;
import org.apache.jackrabbit.oak.plugins.index.IndexHookManager;
import org.apache.jackrabbit.oak.plugins.index.lucene.LuceneIndexHookProvider;
import org.apache.jackrabbit.oak.plugins.index.lucene.LuceneIndexProvider;
import org.apache.jackrabbit.oak.plugins.index.p2.Property2IndexHookProvider;
import org.apache.jackrabbit.oak.plugins.index.p2.Property2IndexProvider;
import org.apache.jackrabbit.oak.plugins.name.NameValidatorProvider;
import org.apache.jackrabbit.oak.plugins.name.NamespaceValidatorProvider;
import org.apache.jackrabbit.oak.plugins.nodetype.DefaultTypeEditor;
import org.apache.jackrabbit.oak.plugins.nodetype.RegistrationValidatorProvider;
import org.apache.jackrabbit.oak.plugins.nodetype.TypeValidatorProvider;
import org.apache.jackrabbit.oak.plugins.nodetype.write.InitialContent;
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
        final Oak oak = new Oak( this.microKernel );
        oak.with( new InitialContent() );
        oak.with( new DefaultTypeEditor() );
        oak.with( new NameValidatorProvider() );
        oak.with( new NamespaceValidatorProvider() );
        oak.with( new TypeValidatorProvider() );
        oak.with( new RegistrationValidatorProvider() );
        oak.with( new ConflictValidatorProvider() );
        oak.with( new AnnotatingConflictHandler() );
        oak.with( new Property2IndexProvider() );
        oak.with( new LuceneIndexProvider() );
        oak.with(
            IndexHookManager.of( new CompositeIndexHookProvider( new Property2IndexHookProvider(), new LuceneIndexHookProvider() ) ) );
        this.repository = new RepositoryImpl( oak.createContentRepository(), null, null );
    }

    @Autowired
    public void setMicroKernel( final MicroKernel microKernel )
    {
        this.microKernel = microKernel;
    }
}