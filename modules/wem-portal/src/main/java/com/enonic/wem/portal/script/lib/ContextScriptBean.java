package com.enonic.wem.portal.script.lib;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import org.mozilla.javascript.Context;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.core.module.ModuleResourcePathResolver;
import com.enonic.wem.portal.script.helper.ScriptHelper;

public final class ContextScriptBean
{
    private final static String NAME = ContextScriptBean.class.getName();

    private ModuleKey module;

    @Inject
    protected ModuleResourcePathResolver pathResolver;

    public ModuleKey getModule()
    {
        return module;
    }

    public void setModule( final ModuleKey module )
    {
        this.module = module;
    }

    public Path getModulePath()
    {
        return this.pathResolver.resolveModulePath( this.module );
    }

    public Path resolveFile( final String name )
    {
        final ModuleResourceKey key = new ModuleResourceKey( this.module, ResourcePath.from( name ) );
        final Path path = this.pathResolver.resolveResourcePath( key );

        if ( Files.isRegularFile( path ) )
        {
            return path;
        }

        throw ScriptHelper.error( "Failed find file [{0}] from module.", key.toString() );
    }

    public void install( final Context context )
    {
        context.putThreadLocal( NAME, this );
    }

    public static void remove( final Context context )
    {
        context.removeThreadLocal( NAME );
    }

    public static ContextScriptBean get()
    {
        return (ContextScriptBean) Context.getCurrentContext().getThreadLocal( NAME );
    }
}
