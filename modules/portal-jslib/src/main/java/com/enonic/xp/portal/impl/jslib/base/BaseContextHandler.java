package com.enonic.xp.portal.impl.jslib.base;

import com.google.common.base.Strings;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;

public abstract class BaseContextHandler
    implements CommandHandler
{
    @Override
    public final Object execute( final CommandRequest req )
    {
        final String branch = req.param( "branch" ).value( String.class );
        if ( Strings.isNullOrEmpty( branch ) )
        {
            return doExecute( req );
        }

        final Context context = ContextBuilder.
            from( ContextAccessor.current() ).
            branch( branch ).
            build();

        return context.callWith( () -> doExecute( req ) );
    }

    protected abstract Object doExecute( final CommandRequest req );
}
