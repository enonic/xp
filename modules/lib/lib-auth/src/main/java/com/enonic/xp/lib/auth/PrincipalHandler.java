package com.enonic.xp.lib.auth;

import com.enonic.xp.script.bean.ScriptBean;

public abstract class PrincipalHandler
    implements ScriptBean
{
    protected String trim( final String value )
    {
        if ( value != null )
        {
            return value.trim();
        }

        return value;
    }
}
