package com.enonic.wem.migrate;

import javax.sql.DataSource;

public interface MigrateContext
{
    public DataSource getDataSource();
}
