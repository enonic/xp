package com.enonic.wem.core.jcr.repository;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.jackrabbit.mk.api.MicroKernel;
import org.apache.jackrabbit.mk.core.MicroKernelImpl;
import org.springframework.beans.factory.FactoryBean;
import javax.inject.Inject;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.config.SystemConfig;

@Component
public final class JcrMicroKernelFactory
    implements FactoryBean<MicroKernel>
{
    private MicroKernelImpl mk;

    private File location;

    private boolean inMemoryRepo = false;

    @Override
    public MicroKernel getObject()
    {
        return this.mk;
    }

    @Override
    public Class<?> getObjectType()
    {
        return MicroKernel.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

    @PostConstruct
    public void init()
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

    @PreDestroy
    public void dispose()
    {
        this.mk.dispose();
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
}
