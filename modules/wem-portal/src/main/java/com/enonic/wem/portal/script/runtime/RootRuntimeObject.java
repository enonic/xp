package com.enonic.wem.portal.script.runtime;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;

import javax.inject.Inject;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.core.module.ModuleResourcePathResolver;

public final class RootRuntimeObject
{
    private Scriptable scope;

    private ModuleKey moduleKey;

    private Path modulePath;

    @Inject
    protected ModuleResourcePathResolver moduleResourcePathResolver;

    public ModuleKey getModule()
    {
        return moduleKey;
    }

    public void setModule( final ModuleKey moduleKey )
    {
        this.moduleKey = moduleKey;
    }

    public Scriptable getScope()
    {
        return scope;
    }

    public void setScope( final Scriptable scope )
    {
        this.scope = scope;
    }

    public FileObject loadFromModule( final String name )
        throws Exception
    {
        final ResourcePath path = ResourcePath.from( name );
        final ModuleResourceKey key = new ModuleResourceKey( this.moduleKey, path );
        final File file = this.moduleResourcePathResolver.resolveResourcePath( key ).toFile();

        if ( !file.isFile() )
        {
            throw error( "Could not find resource [{0}].", key.toString() );
        }

        return new FileObject( file );
    }

    private JavaScriptException error( final String message, final Object... args )
    {
        return ScriptRuntime.throwError( Context.getCurrentContext(), this.scope, MessageFormat.format( message, args ) );
    }
}
