package com.enonic.wem.migrate;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.config.SystemConfig;
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

    @Inject
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

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
    {
        this.dataSource.setUrl( systemConfig.getMigrateJdbcUrl() );
        this.dataSource.setUsername( systemConfig.getMigrateJdbcUser() );
        this.dataSource.setPassword( systemConfig.getMigrateJdbcPassword() );
        this.dataSource.setDriverClassName( systemConfig.getMigrateJdbcDriver() );
        this.enabled = systemConfig.isMigrateEnabled();
    }
}
