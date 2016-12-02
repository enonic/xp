package com.enonic.xp.lib.context;

import java.util.Map;
import java.util.concurrent.Callable;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.security.PrincipalKey;

public final class ContextRunParams
{
    protected String branch;

    protected String username;

    protected String userStore;

    protected PrincipalKey[] principals;

    protected Callable<Object> callback;

    protected Map<String, Object> attributes;

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

    public void setPrincipals( final String[] principals )
    {
        if ( principals == null )
        {
            this.principals = null;
        }
        else
        {
            this.principals = new PrincipalKey[principals.length];
            for ( int i = 0; i < principals.length; i++ )
            {
                this.principals[i] = PrincipalKey.from( principals[i] );
            }
        }
    }

    public void setAttributes( final ScriptValue attributes )
    {
        this.attributes = attributes.getMap();
    }

    public void setCallback( final Callable<Object> callback )
    {
        this.callback = callback;
    }
}
