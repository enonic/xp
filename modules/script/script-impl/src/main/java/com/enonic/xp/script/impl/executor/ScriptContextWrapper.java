package com.enonic.xp.script.impl.executor;

import java.io.Reader;
import java.io.Writer;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleScriptContext;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.script.runtime.DebugSettings;

final class ScriptContextWrapper
    extends SimpleScriptContext
{
    ScriptContext context;

    Resource resource;

    DebugSettings debugSettings;

    @Override
    public void setBindings( final Bindings bindings, final int scope )
    {
        this.context.setBindings( bindings, scope );
    }

    @Override
    public Object getAttribute( final String name, final int scope )
    {
        return this.context.getAttribute( name, scope );
    }

    @Override
    public Object removeAttribute( final String name, final int scope )
    {
        return this.context.removeAttribute( name, scope );
    }

    @Override
    public void setAttribute( final String name, final Object value, final int scope )
    {
        this.context.setAttribute( name, value, scope );
    }

    @Override
    public Writer getWriter()
    {
        return this.context.getWriter();
    }

    @Override
    public Reader getReader()
    {
        return this.context.getReader();
    }

    @Override
    public void setReader( final Reader reader )
    {
        this.context.setReader( reader );
    }

    @Override
    public void setWriter( final Writer writer )
    {
        this.context.setWriter( writer );
    }

    @Override
    public Writer getErrorWriter()
    {
        return this.context.getErrorWriter();
    }

    @Override
    public void setErrorWriter( final Writer writer )
    {
        this.context.setErrorWriter( writer );
    }

    @Override
    public int getAttributesScope( final String name )
    {
        return this.context.getAttributesScope( name );
    }

    @Override
    public Bindings getBindings( final int scope )
    {
        return this.context.getBindings( scope );
    }

    @Override
    public List<Integer> getScopes()
    {
        return this.context.getScopes();
    }

    @Override
    public Object getAttribute( final String name )
    {
        if ( name.equals( ScriptEngine.FILENAME ) )
        {
            return getFileName();
        }

        return super.getAttribute( name );
    }

    private String getFileName()
    {
        if ( this.debugSettings != null )
        {
            return this.debugSettings.scriptName( this.resource );
        }

        return this.resource.getKey().toString();
    }
}
