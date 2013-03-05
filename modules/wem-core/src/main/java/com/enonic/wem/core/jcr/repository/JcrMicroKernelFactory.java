package com.enonic.wem.core.jcr.repository;

import java.io.File;

import org.apache.jackrabbit.mk.api.MicroKernel;
import org.apache.jackrabbit.mk.core.MicroKernelImpl;
import javax.inject.Inject;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.lifecycle.DisposableBean;
import com.enonic.wem.core.lifecycle.InitializingBean;
import com.enonic.wem.core.lifecycle.ProviderFactory;

@Component
public final class JcrMicroKernelFactory
    extends ProviderFactory<MicroKernel>
    implements InitializingBean, DisposableBean
{
    private MicroKernelImpl mk;

    private File location;

    private boolean inMemoryRepo = false;

    public JcrMicroKernelFactory()
    {
        super(MicroKernel.class);
    }

    @Override
    public MicroKernel get()
    {
        return this.mk;
    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
    {
        this.location = new File( systemConfig.getDataDir(), "jcr" );
    }

    public void setInMemoryRepository( final boolean inMemory )
    {
        this.inMemoryRepo = inMemory;
    }

    @Override
    public void destroy()
        throws Exception
    {
        this.mk.dispose();
    }

    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        if ( inMemoryRepo )
        {
            this.mk = new MicroKernelImpl();
        }
        else
        {
            this.mk = new MicroKernelImpl( this.location.getAbsolutePath() );
        }
    }
}
