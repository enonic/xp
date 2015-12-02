package com.enonic.xp.lib.context;

import java.util.concurrent.Callable;

public final class ContextRunParams
{
    protected String branch;

    protected String username;

    protected String userStore;

    protected Callable<Object> callback;

    public void setBranch( final String branch )
    {
        this.branch = branch;
    }

    public void setUsername( final String username )
    {
        this.username = username;
    }

    public void setUserStore( final String userStore )
    {
        this.userStore = userStore;
    }

    public void setCallback( final Callable<Object> callback )
    {
        this.callback = callback;
    }
}
