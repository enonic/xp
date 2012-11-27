package com.enonic.wem.migrate.jcr;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.content.type.ContentTypesInitializer;
import com.enonic.wem.migrate.MigrateTask;

@Component
public class JcrBootstrap
{
    private static final Logger LOG = LoggerFactory.getLogger( JcrBootstrap.class );

    private MigrateTask migrateTask;

    private ContentTypesInitializer contentTypesInitializer;

    @PostConstruct
    private void initializeJcr()
        throws Exception
    {
        LOG.info( "Initializing JCR repository..." );
        migrateTask.migrate();
        LOG.info( "JCR repository initialized" );
        contentTypesInitializer.createSystemTypes();
    }

    @Autowired
    public void setMigrateTask( MigrateTask migrateTask )
    {
        this.migrateTask = migrateTask;
    }

    @Autowired
    public void setContentTypesInitializer( final ContentTypesInitializer contentTypesInitializer )
    {
        this.contentTypesInitializer = contentTypesInitializer;
    }
}
