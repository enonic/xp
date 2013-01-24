package com.enonic.wem.migrate.jdbc;

import java.sql.ResultSet;

public interface JdbcRowCallback
{
    public void onRow( ResultSet result )
        throws Exception;
}
