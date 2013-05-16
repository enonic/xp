package com.enonic.wem.core.jcr.repository;

import javax.inject.Inject;
import javax.jcr.Repository;

import org.apache.jackrabbit.mk.api.MicroKernel;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.RepositoryImpl;
import org.apache.jackrabbit.oak.plugins.commit.AnnotatingConflictHandler;
import org.apache.jackrabbit.oak.plugins.commit.ConflictValidatorProvider;
import org.apache.jackrabbit.oak.plugins.index.CompositeIndexHookProvider;
import org.apache.jackrabbit.oak.plugins.index.IndexHookManager;
import org.apache.jackrabbit.oak.plugins.index.p2.Property2IndexHookProvider;
import org.apache.jackrabbit.oak.plugins.index.p2.Property2IndexProvider;
import org.apache.jackrabbit.oak.plugins.name.NameValidatorProvider;
import org.apache.jackrabbit.oak.plugins.name.NamespaceValidatorProvider;
import org.apache.jackrabbit.oak.plugins.nodetype.DefaultTypeEditor;
import org.apache.jackrabbit.oak.plugins.nodetype.RegistrationValidatorProvider;
import org.apache.jackrabbit.oak.plugins.nodetype.TypeValidatorProvider;
import org.apache.jackrabbit.oak.plugins.nodetype.write.InitialContent;

import com.google.inject.Provider;

import com.enonic.wem.core.lifecycle.InitializingBean;


public final class JcrRepositoryFactory
    implements Provider<Repository>, InitializingBean
{
    private MicroKernel microKernel;

    private Repository repository;

    @Override
    public Repository get()
    {
        return this.repository;
    }

    @Override
    public void afterPropertiesSet()
        throws Exception
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
        oak.with( IndexHookManager.of( new CompositeIndexHookProvider( new Property2IndexHookProvider() ) ) );
        this.repository = new RepositoryImpl( oak.createContentRepository(), null, null );
    }

    @Inject
    public void setMicroKernel( final MicroKernel microKernel )
    {
        this.microKernel = microKernel;
    }
}