package com.enonic.wem.migrate.jcr;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.wem.core.jcr.old.RepositoryRuntimeException;
import com.enonic.wem.migrate.MigrateTask;

//@Component
public class JcrBootstrap
{
    private static final Logger LOG = LoggerFactory.getLogger( JcrBootstrap.class );

    private MigrateTask migrateTask;

    @PostConstruct
    private void initializeJcr()
    {
        LOG.info( "Initializing JCR repository..." );
        try
        {
            migrateTask.migrate();
        }
        catch ( Exception e )
        {
            throw new RepositoryRuntimeException( "Error while initializing JCR repository", e );
        }

        LOG.info( "JCR repository initialized" );
    }

    @Autowired
    public void setMigrateTask( MigrateTask migrateTask )
    {
        this.migrateTask = migrateTask;
    }
}
