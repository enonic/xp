package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import com.enonic.xp.context.Context;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.session.Session;

public final class LogoutHandler
    implements ScriptBean
{
    private Supplier<Context> context;

    public void logout()
    {
        final Session session = this.context.get().getLocalScope().getSession();
        if ( session != null )
        {
            session.invalidate();
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context.getBinding( Context.class );
    }
}
