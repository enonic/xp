package com.enonic.wem.migrate;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public final class DriverManagerDataSource
    implements DataSource
{
    private String url;

    private String user;

    private String password;

    public void setUrl( final String url )
    {
        this.url = url;
    }

    public void setUser( final String user )
    {
        this.user = user;
    }

    public void setPassword( final String password )
    {
        this.password = password;
    }

    public void setDriver( final String driver )
        throws Exception
    {
        Class.forName( driver );
    }

    @Override
    public Connection getConnection()
        throws SQLException
    {
        return getConnection( this.user, this.password );
    }

    @Override
    public Connection getConnection( final String user, final String password )
        throws SQLException
    {
        return DriverManager.getConnection( this.url, user, password );
    }

    @Override
    public PrintWriter getLogWriter()
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLogWriter( final PrintWriter out )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLoginTimeout( final int seconds )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLoginTimeout()
        throws SQLException
    {
        return 0;
    }

    @Override
    public Logger getParentLogger()
        throws SQLFeatureNotSupportedException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap( final Class<T> type )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor( final Class<?> type )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }
}
