package com.enonic.xp.toolbox;

import java.net.ConnectException;

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
        catch ( final Exception e )
        {
            handleError( e );
        }
    }

    private void handleError( final Exception e )
    {
        if ( e instanceof ConnectException )
        {
            error( "Unable to connect to XP server: %s", e.getMessage() );
            return;
        }

        if ( e instanceof ResponseException )
        {
            if ( ( (ResponseException) e ).getResponseCode() == 403 )
            {
                error( "Authentication failed: %s", e.getMessage() );
            }
            else
            {
                error( "Response error: %s", e.getMessage() );
            }
        }

        error( "Unexpected error: %s", e.getMessage() );
    }

    private void error( final String message, final Object... args )
    {
        if ( args.length == 0 )
        {
            System.err.println( message );
        }
        else
        {
            System.err.println( String.format( message, args ) );
        }

        System.exit( -1 );
    }

    protected abstract void execute()
        throws Exception;
}
