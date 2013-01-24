package com.enonic.wem.migrate.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.enonic.wem.migrate.MigrateTask;

public abstract class JdbcMigrateTask
    extends MigrateTask
{
    protected final void query( final String sql, final JdbcRowCallback callback )
        throws Exception
    {
        try (
            final Connection conn = getConnection();
            final Statement stmt = conn.createStatement();
            final ResultSet result = stmt.executeQuery( sql );
        )
        {
            while ( result.next() )
            {
                callback.onRow( result );
            }
        }
    }

    private Connection getConnection()
        throws Exception
    {
        return this.context.getDataSource().getConnection();
    }
}
