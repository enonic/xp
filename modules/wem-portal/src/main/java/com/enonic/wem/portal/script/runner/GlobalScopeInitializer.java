package com.enonic.wem.portal.script.runner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

final class GlobalScopeInitializer
{
    private final static String BASE_PATH = "js/lib/";

    private final static String[] SCRIPTS = { //
        "global", //
        "console", //
    };

    private Scriptable scope;

    public Scriptable initialize()
    {
        final Context context = Context.enter();

        try
        {
            initialize( context );
            return this.scope;
        }
        finally
        {
            Context.exit();
        }
    }

    private void initialize( final Context context )
    {
        this.scope = context.initStandardObjects( null, true );
        evaluateScripts( context, SCRIPTS );
    }

    private void evaluateScripts( final Context context, final String... names )
    {
        for ( final String name : names )
        {
            evaluateScript( context, BASE_PATH + name + ".js" );
        }
    }

    private void evaluateScript( final Context context, final String name )
    {
        try (final InputStream in = loadScript( name ))
        {
            evaluateScript( context, name, in );
        }
        catch ( final IOException e )
        {
            throw new RuntimeException( "Error occured reading script [" + name + "]", e );
        }
    }

    private void evaluateScript( final Context context, final String name, final InputStream in )
        throws IOException
    {
        context.evaluateReader( this.scope, new InputStreamReader( in ), name, 1, null );
    }

    private InputStream loadScript( final String name )
    {
        final InputStream in = getClass().getClassLoader().getResourceAsStream( name );
        if ( in == null )
        {
            throw new RuntimeException( "Could not find script [" + name + "]" );
        }

        return in;
    }
}
