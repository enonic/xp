package com.enonic.wem.core.jcr.repository;

import java.io.File;

import javax.inject.Inject;

import org.apache.jackrabbit.mk.api.MicroKernel;
import org.apache.jackrabbit.mk.core.MicroKernelImpl;

import com.google.inject.Provider;

import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.lifecycle.DisposableBean;


public final class JcrMicroKernelFactory
    implements Provider<MicroKernel>, DisposableBean
{
    private final MicroKernelImpl mk;

    public JcrMicroKernelFactory()
    {
        this.mk = new MicroKernelImpl();
    }

    @Inject
    public JcrMicroKernelFactory( final SystemConfig config )
    {
        final File location = new File( config.getDataDir(), "jcr" );
        this.mk = new MicroKernelImpl( location.getAbsolutePath() );
    }

    @Override
    public MicroKernel get()
    {
        return this.mk;
    }

    @Override
    public void destroy()
        throws Exception
    {
        this.mk.dispose();
    }
}
