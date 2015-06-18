package com.enonic.xp.lib.content;

import com.google.common.base.Strings;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.script.command.CommandRequest;

public abstract class BaseContextHandler
{

    private String branch;

    public void setBranch( final String branch )
    {
        this.branch = branch;
    }

    public final Object execute( final CommandRequest req )
    {
        if ( Strings.isNullOrEmpty( branch ) )
        {
            return doExecute();
        }

        final Context context = ContextBuilder.
            from( ContextAccessor.current() ).
            branch( branch ).
            build();

        return context.callWith( this::doExecute );
    }

    protected abstract Object doExecute();

    protected <T> T checkRequired( final String paramName, final T value )
    {
        if ( value == null )
        {
            throw new IllegalArgumentException( String.format( "Parameter [%s] is required", paramName ) );
        }
        return value;
    }

    protected <T> T valueOrDefault( final T value, final T defValue )
    {
        return value == null ? defValue : value;
    }

}
