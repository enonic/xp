package com.enonic.xp.lib.context;

import java.util.concurrent.Callable;

public final class ContextRunParams
{
    protected String branch;

    protected String user;

    protected Callable<Object> callback;

    public void setBranch( final String branch )
    {
        this.branch = branch;
    }

    public void setUser( final String user )
    {
        this.user = user;
    }

    public void setCallback( final Callable<Object> callback )
    {
        this.callback = callback;
    }
}
