package com.enonic.xp.toolbox;

public abstract class ToolCommand
    implements Runnable
{
    @Override
    public final void run()
    {
        try
        {
            execute();
        }
        catch ( final RuntimeException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    protected abstract void execute()
        throws Exception;
}
