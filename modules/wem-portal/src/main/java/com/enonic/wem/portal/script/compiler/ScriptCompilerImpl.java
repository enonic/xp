package com.enonic.wem.portal.script.compiler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;

import javax.inject.Inject;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;

import com.enonic.wem.portal.script.ScriptException;
import com.enonic.wem.portal.script.cache.ScriptCache;

public final class ScriptCompilerImpl
    implements ScriptCompiler
{
    private final ScriptCache cache;

    @Inject
    public ScriptCompilerImpl( final ScriptCache cache )
    {
        this.cache = cache;
    }

    @Override
    public Script compile( final Context context, final Path path )
    {
        final Script script = this.cache.get( path );
        if ( script != null )
        {
            return script;
        }

        final Script compiled = doCompile( context, path );
        this.cache.put( path, compiled );
        return compiled;
    }

    private Script doCompile( final Context context, final Path path )
    {
        try
        {
            final FileReader reader = new FileReader( path.toFile() );
            return context.compileReader( reader, path.toString(), 1, null );
        }
        catch ( final FileNotFoundException e )
        {
            throw new ScriptException( "Could not find script [" + path.toString() + "]" );
        }
        catch ( final Exception e )
        {
            throw new ScriptException( "Failed to compile script [" + path.toString() + "]", e );
        }
    }
}
