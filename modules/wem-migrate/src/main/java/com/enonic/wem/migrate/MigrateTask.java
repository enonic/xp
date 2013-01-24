package com.enonic.wem.migrate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MigrateTask
{
    protected final Logger logger;

    protected MigrateContext context;

    public MigrateTask()
    {
        this.logger = LoggerFactory.getLogger( getClass() );
    }

    public final void setContext( final MigrateContext context )
    {
        this.context = context;
    }

    public abstract void migrate()
        throws Exception;
}
