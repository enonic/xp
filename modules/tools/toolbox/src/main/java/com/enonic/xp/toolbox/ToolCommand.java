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
        }

        if ( e instanceof ResponseException )
        {
            if ( ( (ResponseException) e ).getResponseCode() == 403 )
            {
                error( "Authentication failed: %s", e.getMessage() );
            }
            else
            {
                error( "Error %d: %s", ( (ResponseException) e ).getResponseCode(), e.getMessage() );
            }
        }
        exception( "Unexpected error: %s", e );
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

    private void exception( final String message, final Exception e )
    {
        System.err.println( String.format( message, e.getMessage() ) );
        e.printStackTrace( System.err );
        System.exit( -1 );
    }

    protected abstract void execute()
        throws Exception;
}
