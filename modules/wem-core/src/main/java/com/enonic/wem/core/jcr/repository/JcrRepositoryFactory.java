package com.enonic.wem.core.jcr.repository;

import javax.inject.Inject;
import javax.jcr.Repository;

import org.apache.jackrabbit.mk.api.MicroKernel;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.RepositoryImpl;
import org.apache.jackrabbit.oak.plugins.commit.ConflictValidatorProvider;
import org.apache.jackrabbit.oak.plugins.commit.JcrConflictHandler;
import org.apache.jackrabbit.oak.plugins.index.nodetype.NodeTypeIndexProvider;
import org.apache.jackrabbit.oak.plugins.index.property.PropertyIndexEditorProvider;
import org.apache.jackrabbit.oak.plugins.index.property.PropertyIndexProvider;
import org.apache.jackrabbit.oak.plugins.name.NameValidatorProvider;
import org.apache.jackrabbit.oak.plugins.name.NamespaceValidatorProvider;
import org.apache.jackrabbit.oak.plugins.nodetype.RegistrationEditorProvider;
import org.apache.jackrabbit.oak.plugins.nodetype.TypeEditorProvider;
import org.apache.jackrabbit.oak.plugins.nodetype.write.InitialContent;
import org.apache.jackrabbit.oak.plugins.version.VersionEditorProvider;
import org.apache.jackrabbit.oak.security.SecurityProviderImpl;
import org.apache.jackrabbit.oak.spi.commit.EditorHook;
import org.apache.jackrabbit.oak.spi.security.SecurityProvider;

import com.google.inject.Provider;


public final class JcrRepositoryFactory
    implements Provider<Repository>
{
    private final Repository repository;

    @Inject
    public JcrRepositoryFactory( final MicroKernel microKernel )
    {
        final SecurityProvider securityProvider = new SecurityProviderImpl();
        final Oak oak = new Oak( microKernel );
        oak.with( new InitialContent() );
        oak.with( JcrConflictHandler.JCR_CONFLICT_HANDLER );
        oak.with( new EditorHook( new VersionEditorProvider() ) );
        oak.with( new NameValidatorProvider() );
        oak.with( new NamespaceValidatorProvider() );
        oak.with( new TypeEditorProvider() );
        oak.with( new RegistrationEditorProvider() );
        oak.with( new ConflictValidatorProvider() );
        oak.with( new PropertyIndexEditorProvider() );
        oak.with( new PropertyIndexProvider() );
        oak.with( new NodeTypeIndexProvider() );
        oak.with( securityProvider );

        this.repository = new RepositoryImpl( oak.createContentRepository(), oak.getExecutorService(), securityProvider );
    }

    @Override
    public Repository get()
    {
        return this.repository;
    }
}