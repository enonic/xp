package com.enonic.wem.api.context;

import com.enonic.wem.api.entity.Workspace;

public class Context
{
    private final Workspace workspace;

    public static Context create( final Workspace workspace )
    {
        return new Context( workspace );
    }

    public Context( final Workspace workspace )
    {
        this.workspace = workspace;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Context ) )
        {
            return false;
        }

        final Context context = (Context) o;

        if ( workspace != null ? !workspace.equals( context.workspace ) : context.workspace != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return workspace != null ? workspace.hashCode() : 0;
    }
}
