package com.enonic.wem.migrate;

import java.io.File;
import java.util.List;

import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.initializer.InitializerTask;

@Component
@Order(100)
public final class DataMigrator
    implements InitializerTask
{
    private final static Logger LOG = LoggerFactory.getLogger( DataMigrator.class );

    private File migrateDir;

    private List<MigrateTask> tasks;

    @Override
    public void initialize()
        throws Exception
    {
        final File dbFile = new File( this.migrateDir, "cms.h2.db" );
        if ( !dbFile.exists() )
        {
            return;
        }

        LOG.info( "Old data exist. Starting to migrate data to new format." );
        doInitialize();
    }

    @Autowired
    public void setTasks( final List<MigrateTask> tasks )
    {
        this.tasks = tasks;
    }

    @Value("${cms.home}/migrate")
    public void setMigrateDir( final File migrateDir )
    {
        this.migrateDir = migrateDir;
    }

    private void doInitialize()
        throws Exception
    {
        final MigrateContextImpl context = newContext();
        executeTasks( context );
    }

    private MigrateContextImpl newContext()
        throws Exception
    {
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setURL( "jdbc:h2:" + new File( this.migrateDir, "cms" ).getAbsolutePath() );
        ds.setUser( "sa" );
        ds.setPassword( "" );

        final MigrateContextImpl context = new MigrateContextImpl();
        context.setDataSource( ds );
        return context;
    }

    private void executeTasks( final MigrateContext context )
        throws Exception
    {
        for ( final MigrateTask task : this.tasks )
        {
            task.setContext( context );
            task.migrate();
        }
    }
}
