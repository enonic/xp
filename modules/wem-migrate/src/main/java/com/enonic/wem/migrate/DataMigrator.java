package com.enonic.wem.migrate;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.initializer.InitializerTask;

@Component
@Order(100)
public final class DataMigrator
    implements InitializerTask
{
    private final static Logger LOG = LoggerFactory.getLogger( DataMigrator.class );

    private List<MigrateTask> tasks;

    private final DriverManagerDataSource dataSource;

    private boolean enabled;

    public DataMigrator()
    {
        this.dataSource = new DriverManagerDataSource();
    }

    @Override
    public void initialize()
        throws Exception
    {
        if ( !this.enabled )
        {
            LOG.info( "Skipping data migration. Not enabled." );
            return;
        }

        LOG.info( "Starting migration of data..." );
        doInitialize();
    }

    @Autowired
    public void setTasks( final List<MigrateTask> tasks )
    {
        this.tasks = tasks;
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
        final MigrateContextImpl context = new MigrateContextImpl();
        context.setDataSource( this.dataSource );
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

    @Value("${cms.migrate.jdbc.url}")
    public void setJdbcUrl( final String url )
    {
        this.dataSource.setUrl( url );
    }

    @Value("${cms.migrate.jdbc.user}")
    public void setJdbcUser( final String user )
    {
        this.dataSource.setUsername( user );
    }

    @Value("${cms.migrate.jdbc.password}")
    public void setJdbcPassword( final String password )
    {
        this.dataSource.setPassword( password );
    }

    @Value("${cms.migrate.jdbc.driver}")
    public void setJdbcDriver( final String driver )
    {
        this.dataSource.setDriverClassName( driver );
    }

    @Value("${cms.migrate.enabled}")
    public void setEnabled( final boolean enabled )
    {
        this.enabled = enabled;
    }
}
