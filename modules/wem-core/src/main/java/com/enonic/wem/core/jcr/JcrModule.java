package com.enonic.wem.core.jcr;

import javax.jcr.Repository;

import org.apache.jackrabbit.mk.api.MicroKernel;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.core.jcr.loader.JcrInitializer;
import com.enonic.wem.core.jcr.provider.JcrSessionProvider;
import com.enonic.wem.core.jcr.provider.JcrSessionProviderImpl;
import com.enonic.wem.core.jcr.repository.JcrMicroKernelFactory;
import com.enonic.wem.core.jcr.repository.JcrRepositoryFactory;

public final class JcrModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( JcrSessionProvider.class ).to( JcrSessionProviderImpl.class ).in( Scopes.SINGLETON );
        bind( MicroKernel.class ).toProvider( JcrMicroKernelFactory.class );
        bind( Repository.class ).toProvider( JcrRepositoryFactory.class );
        bind( JcrInitializer.class ).in( Scopes.SINGLETON );
    }
}
