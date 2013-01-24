package com.enonic.wem.migrate;

import javax.sql.DataSource;

final class MigrateContextImpl
    implements MigrateContext
{
    private DataSource dataSource;

    public void setDataSource( final DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

    @Override
    public DataSource getDataSource()
    {
        return this.dataSource;
    }
}
