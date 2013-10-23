package com.enonic.wem.core.jcr.repository;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.jackrabbit.mk.api.MicroKernel;
import org.apache.jackrabbit.mk.core.MicroKernelImpl;

import com.google.inject.Provider;

import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.lifecycle.LifecycleBean;
import com.enonic.wem.core.lifecycle.RunLevel;

@Singleton
public final class JcrMicroKernelFactory
    extends LifecycleBean
    implements Provider<MicroKernel>
{
    private final MicroKernelImpl mk;

    public JcrMicroKernelFactory()
    {
        super( RunLevel.L1 );
        this.mk = new MicroKernelImpl();
    }

    @Inject
    public JcrMicroKernelFactory( final SystemConfig config )
    {
        super( RunLevel.L1 );
        final File location = new File( config.getDataDir(), "jcr" );
        this.mk = new MicroKernelImpl( location.getAbsolutePath() );
    }

    @Override
    public MicroKernel get()
    {
        return this.mk;
    }

    @Override
    protected void doStart()
        throws Exception
    {
        // Do nothing
    }

    @Override
    protected void doStop()
        throws Exception
    {
        this.mk.dispose();
    }
}
